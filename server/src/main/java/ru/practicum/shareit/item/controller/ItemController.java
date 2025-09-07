package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToOwner;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final String userIdHeader = "X-Sharer-User-Id";
    private final String pathWithItemId = "/{item-id}";

    @Autowired
    private ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader(required = true, name = userIdHeader) Long owner, @RequestBody Item item) {
        log.info("Получен запрос на создание предмета от пользователя c id = {}", owner);
        return ItemDto.toItemDto(service.create(owner, item));
    }

    @PatchMapping(pathWithItemId)
    public ItemDto update(@RequestHeader(required = true, name = userIdHeader) Long owner, @PathVariable("item-id") Long itemId, @RequestBody Item newItem) {
        log.info("Получен запрос на обновление предмета c id = {} от пользователя c id = {}", itemId, owner);
        return ItemDto.toItemDto(service.update(owner, itemId, newItem));
    }

    @GetMapping(pathWithItemId)
    public ItemDtoToOwner get(@RequestHeader(required = true, name = userIdHeader) Long userId, @PathVariable("item-id") Long itemId) {
        log.info("Получен запрос на получение предмета c id = {}", itemId);
        return service.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoWithComments> getAllUserItems(@RequestHeader(required = true, name = userIdHeader) Long owner) {
        log.info("Получен запрос на получение списка всех предметов пользователя c id = {}", owner);
        return service.getAllUserItems(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = true) String text) {
        log.info("Получен запрос на поиск предметов по тексту = {}", text);
        return service.search(text).stream().map(ItemDto::toItemDto).toList();
    }

    @DeleteMapping(pathWithItemId)
    public void deleteItem(@RequestHeader(required = true, name = userIdHeader) Long owner, @PathVariable("item-id") Long itemId) {
        log.info("Получен запрос на удаление предмета с id = {} от пользователя c id = {}", itemId, owner);
        service.delete(owner, itemId);
    }

    @PostMapping(pathWithItemId+"/comment")
    public CommentDto postComment(@RequestHeader(required = true, name = userIdHeader) Long userId, @PathVariable("item-id") Long itemId, @RequestBody Comment comment) {
        log.info("Получен запрос на добавление пользователем с id = {} комментария для предмета с id = {}", userId, itemId);
        return service.postComment(userId, itemId, comment);
    }
}
