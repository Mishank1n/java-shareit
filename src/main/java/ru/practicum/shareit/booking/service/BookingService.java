package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.models.Booking;

import java.util.List;

public interface BookingService {
    Booking create(Long bookerId, Booking booking);

    Booking get(Long bookingId, Long userId);

    Booking responseToBooking(Long bookingId, boolean isApproved, Long userId);

    List<Booking> getAllBookingsOfUser(Long userId, String state);

    List<Booking> getAllBookingsOfItemOwner(Long userId, String state);
}
