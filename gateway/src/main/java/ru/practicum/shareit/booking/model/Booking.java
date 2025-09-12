package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @NotNull(message = "Время начала бронирования не может быть пустым!")
    @FutureOrPresent(message = "Время начала бронирования не может быть в прошлом")
    LocalDateTime start;

    @NotNull(message = "Время окончания бронирования не может быть пустым!")
    @Future(message = "Время окончания бронирования должно быть позже текущего времени")
    LocalDateTime end;

    @NotNull(message = "Id бронируемого предмета не может быть пустым!")
    Long itemId;


}