package ru.practicum.shareit.booking.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "bookings", schema = "public")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull(message = "Время начала бронирования не может быть пустым!")
    @Column(name = "start_time", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
    LocalDateTime start;

    @NotNull(message = "Время окончания бронирования не может быть пустым!")
    @Column(name = "end_time", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
    LocalDateTime end;

    @NotNull(message = "Id бронируемого предмета не может быть пустым!")
    @Transient
    Long itemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    User booker;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    Status status;

}