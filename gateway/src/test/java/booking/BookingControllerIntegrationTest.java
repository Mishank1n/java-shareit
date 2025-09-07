package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItAppGateway;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItAppGateway.class)
@AutoConfigureMockMvc
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private Booking validBooking;
    private ResponseEntity<Object> responseEntity;

    @BeforeEach
    void setUp() {
        validBooking = new Booking();
        validBooking.setItemId(2L);
        validBooking.setStart(LocalDateTime.now().plusHours(1));
        validBooking.setEnd(LocalDateTime.now().plusHours(2));

        responseEntity = new ResponseEntity<>(validBooking, HttpStatus.OK);
    }

    @Test
    @SneakyThrows
    void create_ShouldReturnCreatedBooking() {
        when(bookingClient.create(anyLong(), any(Booking.class)))
                .thenReturn(new ResponseEntity<>(validBooking, HttpStatus.CREATED));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBooking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(2));

        verify(bookingClient, times(1)).create(1L, validBooking);
    }

    @Test
    @SneakyThrows
    void create_ShouldFailValidation_WhenStartInPast() {
        Booking invalidBooking = new Booking();
        invalidBooking.setItemId(2L);
        invalidBooking.setStart(LocalDateTime.now().minusHours(1)); // прошлое
        invalidBooking.setEnd(LocalDateTime.now().plusHours(1));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @SneakyThrows
    void get_ShouldReturnBooking() {
        when(bookingClient.get(1L, 1L)).thenReturn(responseEntity);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(2));

        verify(bookingClient, times(1)).get(1L, 1L);
    }

    @Test
    @SneakyThrows
    void responseToBooking_ShouldReturnBooking() {
        when(bookingClient.responseToBooking(1L, 1L, true)).thenReturn(responseEntity);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(2));

        verify(bookingClient, times(1)).responseToBooking(1L, 1L, true);
    }

    @Test
    @SneakyThrows
    void getAllBookingsOfUser_ShouldReturnList() {
        when(bookingClient.getAllBookingsOfUser(1L, "ALL"))
                .thenReturn(new ResponseEntity<>(new Booking[]{validBooking}, HttpStatus.OK));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId").value(2));

        verify(bookingClient, times(1)).getAllBookingsOfUser(1L, "ALL");
    }

    @Test
    @SneakyThrows
    void getAllBookingsOfItemOwner_ShouldReturnList() {
        when(bookingClient.getAllBookingsOfItemOwner(1L, "ALL"))
                .thenReturn(new ResponseEntity<>(new Booking[]{validBooking}, HttpStatus.OK));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId").value(2));

        verify(bookingClient, times(1)).getAllBookingsOfItemOwner(1L, "ALL");
    }
}
