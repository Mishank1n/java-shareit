package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItAppServer;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = ShareItAppServer.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private final String headerUserId = "X-Sharer-User-Id";

    @Test
    void createBooking_Success() throws Exception {
        Booking booking = new Booking(null,
                LocalDateTime.of(2025, 9, 7, 10, 0),
                LocalDateTime.of(2025, 9, 8, 10, 0),
                1L, new Item(1L, "1", "1", true, new User(), 1L, new ItemRequest()), new User(), null);

        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2025, 9, 7, 10, 0),
                LocalDateTime.of(2025, 9, 8, 10, 0),
                null,
                null,
                new ItemDto()
        );

        when(bookingService.create(eq(1L), any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").value("2025-09-07T10:00:00"))
                .andExpect(jsonPath("$.end").value("2025-09-08T10:00:00"));
    }

    @Test
    void getBookingById_Success() throws Exception {
        Booking booking = new Booking(null,
                LocalDateTime.of(2025, 9, 7, 10, 0),
                LocalDateTime.of(2025, 9, 8, 10, 0),
                1L, new Item(1L, "1", "1", true, new User(), 1L, new ItemRequest()), new User(), null);


        when(bookingService.get(1L, 1L)).thenReturn(booking);

        mockMvc.perform(get("/bookings/1")
                        .header(headerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").value("2025-09-07T10:00:00"))
                .andExpect(jsonPath("$.end").value("2025-09-08T10:00:00"));
    }

    @Test
    void getBookingById_NotFound() throws Exception {
        when(bookingService.get(999L, 1L)).thenThrow(new NotFoundException("Booking not found"));

        mockMvc.perform(get("/bookings/999")
                        .header(headerUserId, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"));
    }

    @Test
    void respondToBooking_Success() throws Exception {
        Booking booking = new Booking(null,
                LocalDateTime.of(2025, 9, 7, 10, 0),
                LocalDateTime.of(2025, 9, 8, 10, 0),
                1L, new Item(1L, "1", "1", true, new User(), 1L, new ItemRequest()), new User(), null);

        when(bookingService.responseToBooking(1L, true, 1L)).thenReturn(booking);

        mockMvc.perform(patch("/bookings/1")
                        .header(headerUserId, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").value("2025-09-07T10:00:00"))
                .andExpect(jsonPath("$.end").value("2025-09-08T10:00:00"));
    }

    @Test
    void getAllBookingsOfUser_Success() throws Exception {
        Booking booking = new Booking(null,
                LocalDateTime.of(2025, 9, 7, 10, 0),
                LocalDateTime.of(2025, 9, 8, 10, 0),
                1L, new Item(1L, "1", "1", true, new User(), 1L, new ItemRequest()), new User(), null);


        when(bookingService.getAllBookingsOfUser(1L, "ALL")).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header(headerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].start").value("2025-09-07T10:00:00"))
                .andExpect(jsonPath("$[0].end").value("2025-09-08T10:00:00"));
    }

    @Test
    void getAllBookingsOfItemOwner_Success() throws Exception {
        Booking booking = new Booking(null,
                LocalDateTime.of(2025, 9, 7, 10, 0),
                LocalDateTime.of(2025, 9, 8, 10, 0),
                1L, new Item(1L, "1", "1", true, new User(), 1L, new ItemRequest()), new User(), null);


        when(bookingService.getAllBookingsOfItemOwner(1L, "ALL")).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header(headerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].start").value("2025-09-07T10:00:00"))
                .andExpect(jsonPath("$[0].end").value("2025-09-08T10:00:00"));
    }

    @Test
    void createBooking_MissingUserIdHeader() throws Exception {
        Booking booking = new Booking();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());
    }
}
