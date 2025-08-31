package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Long owner, Item item);

    Item getById(Long id);

    List<Item> getAllUserItems(Long owner);

    Item update(Long owner, Long itemId, Item newItem);

    List<Item> search(String text);

    void delete(Long owner, Long itemId);
}
