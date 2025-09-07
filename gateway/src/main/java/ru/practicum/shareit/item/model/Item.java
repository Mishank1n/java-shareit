package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Item {
    Long id;
    @NotBlank(message = "Имя не может быть пустым или отсутствовать")
    String name;
    @NotBlank(message = "Описание не может быть пустым или отсутствовать")
    String description;
    @NotNull(message = "Доступность не может отсутствовать")
    Boolean available;
    Long requestId;
}