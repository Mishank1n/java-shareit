package ru.practicum.shareit.booking.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.model.Booking;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final String userIdHeader = "X-Sharer-User-Id";
    private final String pathWithBookingId = "/{booking-id}";

    @Autowired
    private BookingClient client;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(name = userIdHeader) Long userId, @Valid @RequestBody Booking booking) {
        log.info("Получен запрос от пользователя с id = {} на создание бронирования веши с id = {}", userId, booking.getItemId());
        return client.create(userId, booking);
    }

    @GetMapping(pathWithBookingId)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> get(@PathVariable("booking-id") Long bookingId, @RequestHeader(name = userIdHeader) Long userId) {
        log.info("Получен запрос от пользователя с id = {} на получение бронирования с id = {}", userId, bookingId);
        return client.get(userId, bookingId);
    }


    @PatchMapping(pathWithBookingId)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> responseToBooking(@RequestHeader(name = userIdHeader) Long userId, @PathVariable("booking-id") Long bookingId, @RequestParam(name = "approved") boolean isApproved) {
        log.info("Получен запрос от пользователя с id = {} для ответа на бронирования с id = {} с решением {}", userId, bookingId, isApproved);
        return client.responseToBooking(userId, bookingId, isApproved);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllBookingsOfUser(@RequestHeader(name = userIdHeader) Long userId, @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Получен запрос от пользователя с id = {} на получение всех его бронирований с параметром поиска {} ", userId, state);
        return client.getAllBookingsOfUser(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllBookingsOfItemOwner(@RequestHeader(name = userIdHeader) Long userId, @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Получен запрос от пользователя с id = {} на получение всех бронирований его вещей с параметром поиска {} ", userId, state);
        return client.getAllBookingsOfItemOwner(userId, state);
    }
}
