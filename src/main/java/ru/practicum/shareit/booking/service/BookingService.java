package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.models.Booking;

public interface BookingService {
    BookingDto create (Long bookerId, Booking booking);
}
