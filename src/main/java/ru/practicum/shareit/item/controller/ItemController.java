package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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

    private final String USER_ID_HEADER = "X-Sharer-User-Id";
    @Autowired
    private ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader(required = true, name = USER_ID_HEADER) Long owner, @RequestBody Item item) {
        log.info("Получен запрос на создание предмета от пользователя c id = {}", owner);
        return service.create(owner, item);
    }

    @PatchMapping("/{item-id}")
    public ItemDto update(@RequestHeader(required = true, name = USER_ID_HEADER) Long owner, @PathVariable("item-id") Long itemId, @RequestBody Item newItem) {
        log.info("Получен запрос на обновление предмета c id = {} от пользователя c id = {}", itemId, owner);
        return service.update(owner, itemId, newItem);
    }

    @GetMapping("/{item-id}")
    public ItemDto get(@PathVariable("item-id") Long itemId) {
        log.info("Получен запрос на получение предмета c id = {}", itemId);
        return service.getById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(required = true, name = USER_ID_HEADER) Long owner) {
        log.info("Получен запрос на получение списка всех предметов пользователя c id = {}", owner);
        return service.getAllUserItems(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = true) String text) {
        log.info("Получен запрос на поиск предметов по тексту = {}", text);
        return service.search(text);
    }

    @DeleteMapping("/{item-id}")
    public void deleteItem(@RequestHeader(required = true, name = USER_ID_HEADER) Long owner, @PathVariable("item-id") Long itemId) {
        log.info("Получен запрос на удаление предмета с id = {} от пользователя c id = {}", itemId, owner);
        service.delete(owner, itemId);
    }
}
