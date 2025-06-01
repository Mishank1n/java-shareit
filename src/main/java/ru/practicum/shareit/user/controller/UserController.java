package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/{user-id}")
    public UserDto get(@PathVariable("user-id") Long userId) {
        log.info("Получен запрос на получение пользователя с id = {}", userId);
        return service.get(userId);
    }

    @PostMapping()
    public UserDto create(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        return service.create(user);
    }

    @PatchMapping("/{user-id}")
    public UserDto update(@PathVariable("user-id") Long userId, @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя с id = {}", userId);
        return service.update(userId, newUser);
    }

    @DeleteMapping("/{user-id}")
    public void delete(@PathVariable("user-id") Long userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        service.delete(userId);
    }
}
