package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;


@RestController
@RequestMapping(path = "/items")
@AllArgsConstructor
@Slf4j
public class ItemController {

    private final String userIdHeader = "X-Sharer-User-Id";

    private ItemClient client;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(required = true, name = userIdHeader) Long owner, @RequestBody @Valid Item item) {
        log.info("Получен запрос на создание предмета от пользователя c id = {}", owner);
        return client.create(owner, item);
    }

    @GetMapping("/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> get(@RequestHeader(required = true, name = userIdHeader) Long userId, @PathVariable("item-id") Long itemId) {
        log.info("Получен запрос на получение предмета c id = {}", itemId);
        return client.get(userId, itemId);
    }

    @PatchMapping("/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@RequestHeader(required = true, name = userIdHeader) Long owner, @PathVariable("item-id") Long itemId, @RequestBody Item newItem) {
        log.info("Получен запрос на обновление предмета c id = {} от пользователя c id = {}", itemId, owner);
        return client.update(owner, itemId, newItem);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUserItems(@RequestHeader(required = true, name = userIdHeader) Long owner) {
        log.info("Получен запрос на получение списка всех предметов пользователя c id = {}", owner);
        return client.getAllUserItems(owner);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> search(@RequestParam(required = true) String text) {
        log.info("Получен запрос на поиск предметов по тексту = {}", text);
        return client.search(text);
    }

    @DeleteMapping("/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteItem(@RequestHeader(required = true, name = userIdHeader) Long owner, @PathVariable("item-id") Long itemId) {
        log.info("Получен запрос на удаление предмета с id = {} от пользователя c id = {}", itemId, owner);
        return client.delete(owner, itemId);
    }

    @PostMapping("/{item-id}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> postComment(@RequestHeader(required = true, name = userIdHeader) Long userId, @PathVariable("item-id") Long itemId, @RequestBody @Valid Comment comment) {
        log.info("Получен запрос на добавление пользователем с id = {} комментария для предмета с id = {}", userId, itemId);
        return client.postComment(userId, itemId, comment);
    }
}
