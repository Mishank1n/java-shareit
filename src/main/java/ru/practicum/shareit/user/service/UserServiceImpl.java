package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ThingIsAlreadyContain;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public User create(User user) {
        if (user.getEmail() == null) {
            log.error("Адрес электронной почты не может быть пустым!");
            throw new ValidationException("Адрес электронной почты не может быть пустым!");
        }
        if (repository.findAll().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            log.error("Пользователь с адресом электронной почты = {} уже существует", user.getEmail());
            throw new ThingIsAlreadyContain(String.format("Пользователь с адресом электронной почты = %s уже существует", user.getEmail()));
        } else {
            log.info("Создан новый пользователь с почтой = {}", user.getEmail());
            return repository.save(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User get(Long id) {
        User user = repository
                .findById(id)
                .orElseThrow(()->{
                    log.error("Пользователь с id = {} не найден", id);
            return new NotFoundException(String.format("Пользователь с id = %d не найден", id));
                });
        return user;
    }

    @Override
    public User update(Long id, User newUser) {
        if (newUser.getEmail()!=null && repository.existsByEmailAndIdNot(newUser.getEmail(), id)) {
            log.error("Пользователь с адресом электронной почты = {} уже существует", newUser.getEmail());
            throw new ThingIsAlreadyContain(String.format("Пользователь с адресом электронной почты = %s уже существует", newUser.getEmail()));
        }
        User user = repository.findById(id)
                .orElseThrow(()-> {
            log.error("Пользователь с id = {} не найден", id);
            return new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        });
        if (newUser.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
            user.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null && !newUser.getName().equals(user.getName())) {
            user.setName(newUser.getName());
        }
        log.info("Обновлен пользователь с id = {}", id);
        return user;
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        log.info("Пользователь с id = {} был удален", id);
        repository.deleteById(id);
    }
}
