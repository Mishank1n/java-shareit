package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
public interface UserService {
    UserDto create(User user);

    UserDto get(Long id);

    UserDto update(Long id, User newUser);

    void delete(Long id);
}
