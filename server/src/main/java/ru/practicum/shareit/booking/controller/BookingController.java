package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final String userIdHeader = "X-Sharer-User-Id";
    private final String pathWithBookingId = "/{booking-id}";

    @Autowired
    private BookingService service;

    @PostMapping
    public BookingDto create(@RequestHeader(name = userIdHeader) Long id, @RequestBody Booking booking) {
        log.info("Получен запрос от пользователя с id = {} на создание бронирования веши с id = {}", id, booking.getItemId());
        return BookingDto.toBookingDto(service.create(id, booking));
    }

    @GetMapping(pathWithBookingId)
    public BookingDto get(@PathVariable("booking-id") Long bookingId, @RequestHeader(name = userIdHeader) Long userId) {
        log.info("Получен запрос от пользователя с id = {} на получение бронирования с id = {}", userId, bookingId);
        return BookingDto.toBookingDto(service.get(bookingId, userId));
    }

    @PatchMapping(pathWithBookingId)
    public BookingDto responseToBooking(@RequestHeader(name = userIdHeader) Long userId, @PathVariable("booking-id") Long bookingId, @RequestParam(name = "approved") boolean isApproved) {
        log.info("Получен запрос от пользователя с id = {} для ответа на бронирования с id = {} с решением {}", userId, bookingId, isApproved);
        return BookingDto.toBookingDto(service.responseToBooking(bookingId, isApproved, userId));
    }

    @GetMapping()
    public List<BookingDto> getAllBookingsOfUser(@RequestHeader(name = userIdHeader) Long id, @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Получен запрос от пользователя с id = {} на получение всех его бронирований с параметром поиска {} ", id, state);
        return service.getAllBookingsOfUser(id, state).stream().map(BookingDto::toBookingDto).toList();
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsOfItemOwner(@RequestHeader(name = userIdHeader) Long id, @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Получен запрос от пользователя с id = {} на получение всех бронирований его вещей с параметром поиска {} ", id, state);
        return service.getAllBookingsOfItemOwner(id, state).stream().map(BookingDto::toBookingDto).toList();
    }
}
