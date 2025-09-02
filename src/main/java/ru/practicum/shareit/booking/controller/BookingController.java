package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final String userIdHeader = "X-Sharer-User-Id";

    @Autowired
    private BookingService service;

    @PostMapping
    public BookingDto create(@RequestHeader(name = userIdHeader) Long id, @Valid @RequestBody Booking booking) {
        return BookingDto.toBookingDto(service.create(id, booking));
    }

    @GetMapping("/{booking-id}")
    public BookingDto get(@PathVariable("booking-id") Long bookingId, @RequestHeader(name = userIdHeader) Long userId) {
        return BookingDto.toBookingDto(service.get(bookingId, userId));
    }

    @PatchMapping("/{booking-id}")
    public BookingDto responseToBooking(@PathVariable("booking-id") Long bookingId, @RequestParam(name = "approved") boolean isApproved, @RequestHeader(name = userIdHeader) Long userId) {
        return BookingDto.toBookingDto(service.responseToBooking(bookingId, isApproved, userId));
    }

    @GetMapping()
    public List<BookingDto> getAllBookingsOfUser(@RequestHeader(name = userIdHeader) Long id, @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return service.getAllBookingsOfUser(id, state).stream().map(BookingDto::toBookingDto).toList();
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsOfItemOwner(@RequestHeader(name = userIdHeader) Long id, @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return service.getAllBookingsOfItemOwner(id, state).stream().map(BookingDto::toBookingDto).toList();
    }
}
