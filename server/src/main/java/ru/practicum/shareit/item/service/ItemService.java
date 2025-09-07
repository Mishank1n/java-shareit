package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;
import ru.practicum.shareit.item.dto.ItemDtoToOwner;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Long owner, Item item);

    ItemDtoToOwner getById(Long userId, Long itemId);

    Item getByIdWithoutSecondary(Long id);

    List<ItemDtoWithComments> getAllUserItems(Long owner);

    Item update(Long owner, Long itemId, Item newItem);

    List<Item> search(String text);

    CommentDto postComment(Long userId, Long itemId, Comment comment);

    List<ItemDtoForItemRequest> getAllItemsWhichAreAnswerOnRequest(Long requestId);

    void delete(Long owner, Long itemId);
}
