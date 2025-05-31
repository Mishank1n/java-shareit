package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAll();

    Item getItem(Long id);

    Item addItem(Item item);

    void delete(Long id);
}
