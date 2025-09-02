package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

@Service
public interface UserService {
    User create(User user);

    User get(Long id);

    User update(Long id, User newUser);

    void delete(Long id);
}
