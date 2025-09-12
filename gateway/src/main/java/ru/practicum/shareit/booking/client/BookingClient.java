package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {

    public static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Qualifier("bookingRestTemplate") RestTemplate restTemplate) {
        super(restTemplate);

    }

    public ResponseEntity<Object> create(Long userId, Booking booking) {
        return post("", userId, booking);
    }

    public ResponseEntity<Object> get(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> responseToBooking(long userId, Long bookingId, boolean isApproved) {
        return patch("/" + bookingId + "?approved=" + isApproved, userId);
    }

    public ResponseEntity<Object> getAllBookingsOfUser(Long userId, String state) {
        return get("?state=" + state, userId);
    }

    public ResponseEntity<Object> getAllBookingsOfItemOwner(Long userId, String state) {
        return get("/owner?state=" + state, userId);
    }
}
