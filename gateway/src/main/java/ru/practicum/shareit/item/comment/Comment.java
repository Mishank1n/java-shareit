package ru.practicum.shareit.item.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @NotNull(message = "Текст комментария не может отсутствовать")
    @NotBlank(message = "Текст комментария не может быть пустым")
    String text;
}
