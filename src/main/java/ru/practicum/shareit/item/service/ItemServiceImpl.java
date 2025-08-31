package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository repository;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public Item create(Long owner, Item item) {
        User user = userService.get(owner);
        item.setOwner(user);
        if (item.getName() == null || item.getName().isEmpty()) {
            log.error("Имя предмета не может быть пустым");
            throw new ValidationException("Имя предмета не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            log.error("Описание предмета не может быть пустым");
            throw new ValidationException("Описание предмета не может быть пустым");
        }
        if (item.getAvailable() == null) {
            log.error("Доступность предмета не может быть пустой");
            throw new ValidationException("Доступность предмета не может быть пустой");
        }
        log.info("Создан предмет пользователем с id = {}", owner);
        return repository.save(item);
    }

    @Override
    public Item getById(Long id) {
        Item item = repository.findById(id).orElseThrow(()->{
            log.error("Предмет с id = {} не найден!", id);
            return new NotFoundException(String.format("Предмет с id = %d не найден!", id));
        });
        log.info("Найден и возвращен предмет с id = {}", id);
        return item;
    }

    @Override
    public List<Item> getAllUserItems(Long owner) {
        User user = userService.get(owner);
        log.info("Найдены и возвращены все предметы пользователя с id = {}", owner);
        return repository.findByOwnerId(user.getId());
    }

    @Override
    @Transactional
    public Item update(Long owner, Long itemId, Item newItem) {
        User user = userService.get(owner);
        Item item = repository.findById(itemId).orElseThrow(()->{
            log.error("Предмет с id = {} не найден!", itemId);
            return new NotFoundException(String.format("Предмет с id = %d не найден!", itemId));
        });
        if (!item.getOwner().getId().equals(owner)) {
            log.error("Изменить вещь c id = {} может только владелец c id = {} ", itemId, owner);
            throw  new ValidationException(String.format("Изменить вещь c id = %d может только владелец c id = %d ", itemId, owner));
        }
        if (newItem.getName() != null && !newItem.getName().isEmpty() && !item.getName().equals(newItem.getName())) {
            item.setName(newItem.getName());
        }
        if (newItem.getDescription() != null && !newItem.getDescription().isEmpty() && !item.getDescription().equals(newItem.getDescription())) {
            item.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null && !item.getAvailable().equals(newItem.getAvailable())) {
            item.setAvailable(newItem.getAvailable());
        }
        log.info("Обновлены данные предмета с id = {}", itemId);
        return item;
    }

    @Override
    public List<Item> search(String text) {
        if (text.isEmpty()) {
            log.info("Получена пустая строка запроса");
            return new ArrayList<>();
        }
        log.info("Найдены и возвращены все предметы с текстом = {}", text);
        return repository.findAll().stream().filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()) || item.getName().toLowerCase().contains(text.toLowerCase())).filter(Item::getAvailable).toList();
    }

    @Override
    @Transactional
    public void delete(Long ownerId, Long itemId) {
        User owner = userService.get(ownerId);
        Item item = repository.findById(itemId).orElseThrow(()-> {
            log.error("Предмет с id = {} не найден!", itemId);
            return new NotFoundException(String.format("Предмет с id = %d не найден!", itemId));
        });
        if (!item.getOwner().getId().equals(ownerId)) {
            log.error("Удалить вещь c id = {} может только владелец c id = {} ", itemId, owner);
            throw new ValidationException(String.format("Удалить вещь c id = %d может только владелец c id = %d ", itemId, owner));
        }
        log.info("Предмет с id = {} был удален", itemId);
        repository.deleteById(itemId);
    }
}
