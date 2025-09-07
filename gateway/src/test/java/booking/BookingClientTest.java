package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.model.Booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class BookingClientTest {

    private RestTemplate restTemplate;
    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        bookingClient = new BookingClient(restTemplate);
    }

    @Test
    void create_SendsPostWithHeaderAndBody() {
        Long userId = 1L;
        Booking booking = new Booking();
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        bookingClient.create(userId, booking);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), captor.capture(), eq(Object.class));

        HttpEntity captured = captor.getValue();
        assertEquals(booking, captured.getBody());
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void get_SendsGetWithHeader() {
        Long userId = 1L;
        Long bookingId = 2L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        bookingClient.get(userId, bookingId);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), captor.capture(), eq(Object.class));

        assertTrue(urlCaptor.getValue().endsWith("/" + bookingId));
        HttpEntity captured = captor.getValue();
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void responseToBooking_SendsPatchWithHeader() {
        Long userId = 1L;
        Long bookingId = 2L;
        boolean approved = true;
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        bookingClient.responseToBooking(userId, bookingId, approved);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.PATCH), captor.capture(), eq(Object.class));

        assertTrue(urlCaptor.getValue().endsWith("/" + bookingId + "?approved=" + approved));
        HttpEntity captured = captor.getValue();
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void getAllBookingsOfUser_SendsGetWithHeader() {
        Long userId = 1L;
        String state = "ALL";
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        bookingClient.getAllBookingsOfUser(userId, state);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), captor.capture(), eq(Object.class));

        assertTrue(urlCaptor.getValue().contains("?state=" + state));
        HttpEntity captured = captor.getValue();
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void getAllBookingsOfItemOwner_SendsGetWithHeader() {
        Long userId = 1L;
        String state = "FUTURE";
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        bookingClient.getAllBookingsOfItemOwner(userId, state);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), captor.capture(), eq(Object.class));

        assertTrue(urlCaptor.getValue().contains("/owner?state=" + state));
        HttpEntity captured = captor.getValue();
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }
}
