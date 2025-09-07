package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {

    Long id;

    @NotBlank(message = "Описание запроса не может быть пустым или отсутствовать ")
    String description;

    LocalDateTime created;
}
