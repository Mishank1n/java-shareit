package ru.practicum.shareit.booking.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    Long id;
    @NonNull
    LocalDateTime start;
    @NonNull
    LocalDateTime end;
    Item item;
    User booker;
    Status status;
}
