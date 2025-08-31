package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.service.BookingService;

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
    public BookingDto create(@RequestHeader(required = true, name = userIdHeader) Long id, @RequestBody Booking booking){
        return service.create(id, booking);
    }
}
