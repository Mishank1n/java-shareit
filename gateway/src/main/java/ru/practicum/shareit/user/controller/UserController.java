package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.model.User;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserClient client;
    private final String pathWithUserId = "/{user-id}";

    @GetMapping(pathWithUserId)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> get(@PathVariable("user-id") Long userId) {
        log.info("Получен запрос на получение пользователя с id = {}", userId);
        return client.get(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody @Valid User user) {
        log.info("Получен запрос на создание пользователя");
        return client.create(user);
    }

    @PatchMapping(pathWithUserId)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@PathVariable("user-id") Long userId, @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя с id = {}", userId);
        return client.update(userId, newUser);
    }

    @DeleteMapping(pathWithUserId)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> delete(@PathVariable("user-id") Long userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        return client.delete(userId);
    }
}
