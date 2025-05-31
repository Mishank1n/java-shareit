package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ThingIsAlreadyContain;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDto create(User user) {
        if (user.getEmail() == null) {
            log.error("Адрес электронной почты не может быть пустым!");
            throw new ValidationException("Адрес электронной почты не может быть пустым!");
        }
        if (repository.getAll().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            log.error("Пользователь с адресом электронной почты = {} уже существует", user.getEmail());
            throw new ThingIsAlreadyContain(String.format("Пользователь с адресом электронной почты = %s уже существует", user.getEmail()));
        } else {
            log.info("Создан новый пользователь с почтой = {}", user.getEmail());
            return UserDto.toUserDto(repository.create(user));
        }
    }

    @Override
    public UserDto get(Long id) {
        if (repository.getById(id) != null) {
            log.info("Найден и возвращен пользователь с id = {}", id);
            return UserDto.toUserDto(repository.getById(id));
        } else {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    @Override
    public UserDto update(Long id, User newUser) {
        if (repository.getAll().stream().anyMatch(user1 -> user1.getEmail().equals(newUser.getEmail()))) {
            log.error("Пользователь с адресом электронной почты = {} уже существует", newUser.getEmail());
            throw new ThingIsAlreadyContain(String.format("Пользователь с адресом электронной почты = %s уже существует", newUser.getEmail()));
        }
        if (repository.getById(id) != null) {
            User user = repository.getById(id);
            if (newUser.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
                user.setEmail(newUser.getEmail());
            }
            if (newUser.getName() != null && !newUser.getName().equals(user.getName())) {
                user.setName(newUser.getName());
            }
            log.info("Обновлен пользователь с id = {}", id);
            return UserDto.toUserDto(user);
        } else {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    @Override
    public void delete(Long id) {
        if (repository.getById(id) != null) {
            log.info("Пользователь с id = {} был удален", id);
            repository.delete(id);
        } else {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }
}
