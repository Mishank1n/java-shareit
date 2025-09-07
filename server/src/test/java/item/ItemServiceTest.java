package item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;
import ru.practicum.shareit.item.dto.ItemDtoToOwner;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository repository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        owner = new User(1L, "John", "john@example.com");
        item = new Item(1L, "Drill", "Power drill", true, owner, null, null);
    }

    // ======== create ========
    @Test
    void createItem_Success() {
        when(userService.get(1L)).thenReturn(owner);
        when(repository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Item created = itemService.create(1L, item);

        assertEquals("Drill", created.getName());
        assertEquals(owner, created.getOwner());
    }

    @Test
    void createItem_WithRequestId() {
        item.setRequestId(5L);
        when(userService.get(1L)).thenReturn(owner);
        when(itemRequestRepository.findById(5L)).thenReturn(Optional.of(new ItemRequest()));
        when(repository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Item created = itemService.create(1L, item);

        assertNotNull(created.getRequest());
    }

    // ======== getById ========
    @Test
    void getById_Success_Owner() {
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBooking(anyLong(), any())).thenReturn(Optional.of(new Booking()));
        when(bookingRepository.findNextBooking(anyLong(), any())).thenReturn(Optional.of(new Booking()));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of());

        ItemDtoToOwner dto = itemService.getById(1L, 1L);

        assertEquals(item.getId(), dto.getId());
    }

    @Test
    void getById_Success_NotOwner() {
        User other = new User(2L, "Alice", "alice@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of());

        ItemDtoToOwner dto = itemService.getById(2L, 1L);
        assertEquals(item.getId(), dto.getId());
    }

    @Test
    void getByIdWithoutSecondary_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemService.getByIdWithoutSecondary(999L));
        assertTrue(ex.getMessage().contains("не найден"));
    }

    @Test
    void getById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemService.getById(owner.getId(), 1L));

        assertTrue(ex.getMessage().contains("Предмет с id = 1 не найден!"));
        verify(repository).findById(1L);
    }


    // ======== getAllUserItems ========
    @Test
    void getAllUserItems_Success() {
        when(userService.get(1L)).thenReturn(owner);
        when(repository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of());

        List<ItemDtoWithComments> items = itemService.getAllUserItems(1L);

        assertEquals(1, items.size());
        assertEquals(item.getName(), items.get(0).getName());
    }

    // ======== update ========
    @Test
    void updateItem_Success() {
        Item newItem = new Item(null, "Drill Updated", "Updated description", false, null, null, null);
        when(userService.get(1L)).thenReturn(owner);
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        Item updated = itemService.update(1L, 1L, newItem);

        assertEquals("Drill Updated", updated.getName());
        assertEquals("Updated description", updated.getDescription());
        assertFalse(updated.getAvailable());
    }

    @Test
    void updateItem_SuccessNotUpdate() {
        Item newItem = new Item(1L, "Drill Updated", "Updated description", false, owner, 1L, new ItemRequest());
        when(userService.get(1L)).thenReturn(owner);
        when(repository.findById(1L)).thenReturn(Optional.of(newItem));

        Item updated = itemService.update(1L, 1L, newItem);

        assertEquals("Drill Updated", updated.getName());
        assertEquals("Updated description", updated.getDescription());
        assertFalse(updated.getAvailable());
    }

    @Test
    void updateItem_NotOwner() {
        Item newItem = new Item(null, "Drill Updated", null, null, null, null, null);
        when(userService.get(2L)).thenReturn(new User(2L, "Alice", "alice@example.com"));
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        ValidationException ex = assertThrows(ValidationException.class, () -> itemService.update(2L, 1L, newItem));
        assertTrue(ex.getMessage().contains("может только владелец"));
    }

    @Test
    void updateItem_NotFound() {
        when(userService.get(1L)).thenReturn(owner);
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Item newItem = new Item();
        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemService.update(1L, 999L, newItem));
        assertTrue(ex.getMessage().contains("не найден"));
    }

    // ======== search ========
    @Test
    void searchItems_EmptyText() {
        List<Item> result = itemService.search("");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_Found() {
        when(repository.search("drill")).thenReturn(List.of(item));

        List<Item> result = itemService.search("drill");
        assertEquals(1, result.size());
    }

    // ======== postComment ========
    @Test
    void postComment_Success() {
        Comment comment = new Comment(null, "Great!", null, null, null);
        when(userService.get(1L)).thenReturn(owner);
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllBookerWhichTakeItem(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(owner));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommentDto dto = itemService.postComment(1L, 1L, comment);

        assertEquals("Great!", dto.getText());
        assertEquals(owner.getName(), dto.getAuthorName());
    }

    @SneakyThrows
    @Test
    void postComment_NotBooked() {
        Comment comment = new Comment();
        when(userService.get(2L)).thenReturn(new User(2L, "Alice", "alice@example.com"));
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllBookerWhichTakeItem(1L, LocalDateTime.now())).thenReturn(List.of(owner));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.postComment(2L, 1L, comment));
        assertTrue(ex.getMessage().contains("не брал в аренду"));
    }

    @Test
    void postComment_ItemNotFound() {
        when(userService.get(1L)).thenReturn(owner);
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Comment comment = new Comment();
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.postComment(1L, 999L, comment));
        assertTrue(ex.getMessage().contains("не найден"));
    }

    // ======== delete ========
    @Test
    void deleteItem_Success() {
        when(userService.get(1L)).thenReturn(owner);
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> itemService.delete(1L, 1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void deleteItem_NotOwner() {
        // Пользователь, который не владелец
        User notOwner = new User(2L, "Alice", "alice@example.com");

        when(userService.get(2L)).thenReturn(notOwner);
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        ValidationException ex = assertThrows(ValidationException.class, () -> itemService.delete(2L, 1L));
        assertTrue(ex.getMessage().contains("может только владелец"));
    }

    @Test
    void deleteItem_NotFound() {
        when(userService.get(1L)).thenReturn(owner);
        when(repository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemService.delete(1L, 999L));
        assertTrue(ex.getMessage().contains("не найден"));
    }

    // ======== getAllItemsWhichAreAnswerOnRequest ========
    @Test
    void getAllItemsWhichAreAnswerOnRequest_Success() {
        when(repository.findByRequestId(5L)).thenReturn(List.of(item));

        List<ItemDtoForItemRequest> list = itemService.getAllItemsWhichAreAnswerOnRequest(5L);

        assertEquals(1, list.size());
    }

    @Test
    void getAllItemsWhichAreAnswerOnRequest_Empty() {
        when(repository.findByRequestId(5L)).thenReturn(List.of());

        List<ItemDtoForItemRequest> list = itemService.getAllItemsWhichAreAnswerOnRequest(5L);

        assertTrue(list.isEmpty());
    }

    @Test
    void getByIdWithoutSecondary_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        Item result = itemService.getByIdWithoutSecondary(1L);

        assertEquals(item, result);
        verify(repository).findById(1L);
    }


}
