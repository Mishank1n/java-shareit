package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.model.ItemRequest;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final String userIdHeader = "X-Sharer-User-Id";

    @Autowired
    private ItemRequestClient client;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(name = userIdHeader) Long userId, @RequestBody @Valid ItemRequest itemRequest) {
        log.info("Получен запрос на создание просьбы о предмете от пользователя с id = {}", userId);
        return client.create(userId, itemRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUserItemRequestsAndOrderByCreatedDesc(@RequestHeader(name = userIdHeader) Long userId) {
        return client.getAllUserRequestsAndOrderByCreatedDesc(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRequestsNotByUserAndOrderByCreatedDesc(@RequestHeader(name = userIdHeader) Long userId) {
        return client.getAllRequestsNotByUserAndSortedByCreatedDesc(userId);
    }

    @GetMapping("/{request-id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getByIdWithAnswers(@RequestHeader(name = userIdHeader) Long userId, @PathVariable("request-id") Long requestId) {
        return client.getByIdWithAnswers(userId, requestId);
    }
}