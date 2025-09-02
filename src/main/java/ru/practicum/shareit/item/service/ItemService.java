package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDtoWithAddendum;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Long owner, Item item);

    ItemDtoWithAddendum getById(Long id);

    Item getByIdWithoutSecondary(Long id);

    List<ItemDtoWithAddendum> getAllUserItems(Long owner);

    Item update(Long owner, Long itemId, Item newItem);

    List<Item> search(String text);

    CommentDto postComment(Long userId, Long itemId, Comment comment);

    void delete(Long owner, Long itemId);
}
