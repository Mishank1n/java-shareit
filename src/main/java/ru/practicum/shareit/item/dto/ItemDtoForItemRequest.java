package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemDtoForItemRequest {
    Long id;
    String name;
    Long owner;

    public static ItemDtoForItemRequest toItemDtoForItemRequest(Item item){
        return new ItemDtoForItemRequest(item.getId(), item.getName(), item.getOwner().getId());
    }
}
