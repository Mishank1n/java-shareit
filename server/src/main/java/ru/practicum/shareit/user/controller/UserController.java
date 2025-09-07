package ru.practicum.shareit.user.controller;

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
        return UserDto.toUserDto(service.get(userId));
    }

    @PostMapping()
    public UserDto create(@RequestBody User user) {
        return UserDto.toUserDto(service.create(user));
    }

    @PatchMapping("/{user-id}")
    public UserDto update(@PathVariable("user-id") Long userId, @RequestBody User newUser) {
        return UserDto.toUserDto(service.update(userId, newUser));
    }

    @DeleteMapping("/{user-id}")
    public void delete(@PathVariable("user-id") Long userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        service.delete(userId);
    }
}
