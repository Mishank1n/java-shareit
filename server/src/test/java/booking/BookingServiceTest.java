package booking;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.models.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository repository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemServiceImpl itemService;

    @Mock
    private EntityManager entityManager;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;

    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;
    private Booking waitingBooking;
    private Booking rejectedBooking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        booker = new User(1L, "Booker", "booker@mail.com");
        owner = new User(2L, "Owner", "owner@mail.com");
        item = new Item(1L, "Item", "Desc", true, owner, 1L, new ItemRequest());
        booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId(), item, booker,
                Status.WAITING);

        LocalDateTime now = LocalDateTime.now();

        pastBooking = new Booking();
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setStart(now.minusDays(5));
        pastBooking.setEnd(now.minusDays(1));

        currentBooking = new Booking();
        currentBooking.setStatus(Status.APPROVED);
        currentBooking.setStart(now.minusHours(1));
        currentBooking.setEnd(now.plusHours(1));

        futureBooking = new Booking();
        futureBooking.setStatus(Status.APPROVED);
        futureBooking.setStart(now.plusDays(1));
        futureBooking.setEnd(now.plusDays(2));

        waitingBooking = new Booking();
        waitingBooking.setStatus(Status.WAITING);
        waitingBooking.setStart(now.plusDays(1));
        waitingBooking.setEnd(now.plusDays(2));

        rejectedBooking = new Booking();
        rejectedBooking.setStatus(Status.REJECTED);
        rejectedBooking.setStart(now.plusDays(1));
        rejectedBooking.setEnd(now.plusDays(2));
    }

    @Test
    void create_Success() {
        when(userService.get(1L)).thenReturn(booker);
        when(itemService.getByIdWithoutSecondary(1L)).thenReturn(item);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.create(1L, booking);

        assertEquals(Status.WAITING, result.getStatus());
        assertEquals(booker, result.getBooker());
        assertEquals(item, result.getItem());
        verify(repository).save(booking);
    }

    @Test
    void create_InvalidTime_ThrowsException() {
        booking.setEnd(booking.getStart());
        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.create(1L, booking));
        assertTrue(ex.getMessage().contains("не может быть позже времени окончания"));
    }

    @Test
    void create_ItemNotAvailable_ThrowsException() {
        item.setAvailable(false);
        when(userService.get(1L)).thenReturn(booker);
        when(itemService.getByIdWithoutSecondary(1L)).thenReturn(item);

        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.create(1L, booking));
        assertTrue(ex.getMessage().contains("должен быть доступен"));
    }

    @Test
    void get_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.get(1L, 1L);

        assertEquals(booking, result);
    }

    @Test
    void get_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> bookingService.get(1L, 1L));
        assertTrue(ex.getMessage().contains("не найдено"));
    }

    @Test
    void get_UserNotAllowed_ThrowsException() {
        User otherUser = new User(3L, "Other", "other@mail.com");
        booking.setBooker(booker);
        booking.setItem(item);

        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.get(1L, 3L));
        assertTrue(ex.getMessage().contains("только пользователь"));
    }

    @Test
    void responseToBooking_Approve_Success() {
        when(repository.findByIdWithItemAndOwnerAndBooker(1L)).thenReturn(Optional.of(booking));
        when(repository.findByIdWithItemAndOwnerAndBooker(1L)).thenReturn(Optional.of(booking));

        bookingService.responseToBooking(1L, true, owner.getId());

        verify(repository).updateStatusToApprove(1L);
    }

    @Test
    void responseToBooking_Reject_Success() {
        when(repository.findByIdWithItemAndOwnerAndBooker(1L)).thenReturn(Optional.of(booking));
        when(repository.findByIdWithItemAndOwnerAndBooker(1L)).thenReturn(Optional.of(booking));

        bookingService.responseToBooking(1L, false, owner.getId());

        verify(repository).updateStatusToRejected(1L);
    }

    @Test
    void responseToBooking_NotFound() {
        when(repository.findByIdWithItemAndOwnerAndBooker(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> bookingService.responseToBooking(1L, true, owner.getId()));
    }

    @Test
    void responseToBooking_NotOwner_ThrowsException() {
        when(repository.findByIdWithItemAndOwnerAndBooker(1L)).thenReturn(Optional.of(booking));

        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.responseToBooking(1L, true, 3L));
    }

    @Test
    void getAllBookingsOfUser_Success() {
        when(userService.get(1L)).thenReturn(booker);
        when(repository.findAllByBookerIdOrderByStartDate(1L)).thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsOfUser(1L, "WAITING");

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingsOfItemOwner_Success() {
        when(itemService.getAllUserItems(owner.getId())).thenReturn(List.of(ItemDtoWithComments.toItemDtoWithComments(item, List.of())));
        when(repository.findAllByItemOwnerIdOrderByStartDate(owner.getId())).thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsOfItemOwner(owner.getId(), "WAITING");

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingsOfItemOwner_NoItems_ThrowsException() {
        when(itemService.getAllUserItems(owner.getId())).thenReturn(List.of());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsOfItemOwner(owner.getId(), "WAITING"));
    }

    @Test
    void getAllBookingsOfItemOwner_NoBookings_ThrowsException() {
        when(itemService.getAllUserItems(owner.getId())).thenReturn(List.of(ItemDtoWithComments.toItemDtoWithComments(item, List.of())));
        when(repository.findAllByItemOwnerIdOrderByStartDate(owner.getId())).thenReturn(List.of());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsOfItemOwner(owner.getId(), "WAITING"));
    }

    @Test
    void testFilterCurrent() {
        List<Booking> bookings = List.of(pastBooking, currentBooking, futureBooking);
        List<Booking> filtered = bookingService.filterWithState(bookings, "CURRENT");
        assertEquals(1, filtered.size());
        assertEquals(currentBooking, filtered.get(0));
    }

    @Test
    void testFilterPast() {
        List<Booking> bookings = List.of(pastBooking, currentBooking, futureBooking);
        List<Booking> filtered = bookingService.filterWithState(bookings, "PAST");
        assertEquals(1, filtered.size());
        assertEquals(pastBooking, filtered.get(0));
    }

    @Test
    void testFilterFuture() {
        List<Booking> bookings = List.of(pastBooking, currentBooking, futureBooking);
        List<Booking> filtered = bookingService.filterWithState(bookings, "FUTURE");
        assertEquals(1, filtered.size());
        assertEquals(futureBooking, filtered.get(0));
    }

    @Test
    void testFilterWaiting() {
        List<Booking> bookings = List.of(waitingBooking, rejectedBooking);
        List<Booking> filtered = bookingService.filterWithState(bookings, "WAITING");
        assertEquals(1, filtered.size());
        assertEquals(waitingBooking, filtered.get(0));
    }

    @Test
    void testFilterRejected() {
        List<Booking> bookings = List.of(waitingBooking, rejectedBooking);
        List<Booking> filtered = bookingService.filterWithState(bookings, "REJECTED");
        assertEquals(1, filtered.size());
        assertEquals(rejectedBooking, filtered.get(0));
    }

    @Test
    void testFilterDefault() {
        List<Booking> bookings = List.of(pastBooking, currentBooking, futureBooking, waitingBooking, rejectedBooking);
        List<Booking> filtered = bookingService.filterWithState(bookings, "UNKNOWN");
        assertEquals(5, filtered.size()); // вернет все без фильтрации
    }


}
