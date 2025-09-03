package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemRequestDtoWithoutAnswers {
    Long id;
    String description;
    LocalDateTime created;

    public static ItemRequestDtoWithoutAnswers toItemRequestDtoWithoutAnswers(ItemRequest request){
        return new ItemRequestDtoWithoutAnswers(request.getId(), request.getDescription(), request.getCreated());
    }
}
