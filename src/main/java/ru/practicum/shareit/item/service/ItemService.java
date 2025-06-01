package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(Long owner, Item item);

    ItemDto getById(Long id);

    List<ItemDto> getAllUserItems(Long owner);

    ItemDto update(Long owner, Long itemId, Item newItem);

    List<ItemDto> search(String text);

    void delete(Long owner, Long itemId);
}
