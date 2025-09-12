package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDtoWithAnswers {
    Long id;
    String description;
    LocalDateTime created;
    List<ItemDtoForItemRequest> items;

    public static ItemRequestDtoWithAnswers toItemRequestDtoWithAnswers(ItemRequest itemRequest, List<ItemDtoForItemRequest> answersOnRequest) {
        return new ItemRequestDtoWithAnswers(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), answersOnRequest);
    }
}
