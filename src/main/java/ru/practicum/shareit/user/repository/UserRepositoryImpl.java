package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Long, User> repository = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(getNewId());
        repository.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(Long id) {
        return repository.getOrDefault(id, null);
    }

    @Override
    public void delete(Long id) {
        repository.remove(id);
    }

    @Override
    public User update(Long id, User user) {
        return user;
    }

    @Override
    public List<User> getAll() {
        return repository.values().stream().toList();
    }

    private Long getNewId() {
        Long id = repository.keySet().stream().mapToLong(Long::longValue).max().orElse(0);
        return ++id;
    }
}
