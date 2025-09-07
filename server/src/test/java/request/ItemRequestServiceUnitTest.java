package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestServiceUnitTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository repository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    private User user1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = new User(1L, "John", "john@example.com");
    }

    // ======== create ========
    @Test
    void createRequest_Success() {
        ItemRequest request = new ItemRequest();
        request.setDescription("Need a drill");

        when(userService.get(user1.getId())).thenReturn(user1);
        when(repository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ItemRequest savedRequest = itemRequestService.create(user1.getId(), request);

        assertNotNull(savedRequest.getId());
        assertEquals("Need a drill", savedRequest.getDescription());
        assertEquals(user1, savedRequest.getRequestor());
        assertNotNull(savedRequest.getCreated());
    }

    // ======== getById ========
    @Test
    void getById_Success() {
        ItemRequest request = new ItemRequest(1L, "Request 1", user1, LocalDateTime.now());

        when(repository.findById(1L)).thenReturn(Optional.of(request));

        ItemRequest found = itemRequestService.getById(1L);

        assertEquals(1L, found.getId());
        assertEquals("Request 1", found.getDescription());
    }

    @Test
    void getById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemRequestService.getById(999L));
        assertTrue(ex.getMessage().contains("не найден"));
    }

    // ======== getAllUserRequestsAndOrderByCreatedDesc ========
    @Test
    void getAllUserRequestsAndOrderByCreatedDesc_WithRequests() {
        ItemRequest request1 = new ItemRequest(1L, "Request 1", user1, LocalDateTime.now());
        ItemRequest request2 = new ItemRequest(2L, "Request 2", user1, LocalDateTime.now().plusMinutes(1));

        when(userService.get(user1.getId())).thenReturn(user1);
        when(repository.findByRequestorIdOrderByCreatedDesc(user1.getId())).thenReturn(List.of(request2, request1));
        when(itemService.getAllItemsWhichAreAnswerOnRequest(anyLong())).thenReturn(List.of());

        List<ItemRequestDtoWithAnswers> requests = itemRequestService.getAllUserRequestsAndOrderByCreatedDesc(user1.getId());

        assertEquals(2, requests.size());
        assertEquals("Request 2", requests.get(0).getDescription());
        assertEquals("Request 1", requests.get(1).getDescription());
    }

    @Test
    void getAllUserRequestsAndOrderByCreatedDesc_Empty() {
        when(userService.get(user1.getId())).thenReturn(user1);
        when(repository.findByRequestorIdOrderByCreatedDesc(user1.getId())).thenReturn(List.of());

        List<ItemRequestDtoWithAnswers> requests = itemRequestService.getAllUserRequestsAndOrderByCreatedDesc(user1.getId());

        assertTrue(requests.isEmpty());
    }

    // ======== getAllRequestsNotByUserAndSortedByCreatedDesc ========
    @Test
    void getAllRequestsNotByUserAndSortedByCreatedDesc_WithRequests() {
        User user2 = new User(2L, "Alice", "alice@example.com");
        ItemRequest request2 = new ItemRequest(2L, "Request 2", user2, LocalDateTime.now());

        when(userService.get(user1.getId())).thenReturn(user1);
        when(repository.findByRequestorIdNotOrderByCreatedDesc(user1.getId())).thenReturn(List.of(request2));

        List<ItemRequestDtoWithoutAnswers> requests = itemRequestService.getAllRequestsNotByUserAndSortedByCreatedDesc(user1.getId());

        assertEquals(1, requests.size());
        assertEquals("Request 2", requests.get(0).getDescription());
    }

    @Test
    void getAllRequestsNotByUserAndSortedByCreatedDesc_Empty() {
        when(userService.get(user1.getId())).thenReturn(user1);
        when(repository.findByRequestorIdNotOrderByCreatedDesc(user1.getId())).thenReturn(List.of());

        List<ItemRequestDtoWithoutAnswers> requests = itemRequestService.getAllRequestsNotByUserAndSortedByCreatedDesc(user1.getId());

        assertTrue(requests.isEmpty());
    }
}
