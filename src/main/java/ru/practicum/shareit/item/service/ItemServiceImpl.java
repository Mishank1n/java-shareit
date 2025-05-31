package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository repository;

    @Autowired
    private UserService userService;

    @Override
    public ItemDto create(Long owner, Item item) {
        UserDto userDto = userService.get(owner);
        item.setOwner(userDto.getId());
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
        return ItemDto.toItemDto(repository.addItem(item));
    }

    @Override
    public ItemDto getById(Long id) {
        if (repository.getItem(id) == null) {
            log.error("Предмет с id = {} не найден!", id);
            throw new NotFoundException(String.format("Предмет с id = %d не найден!", id));
        }
        log.info("Найден и возвращен предмет с id = {}", id);
        return ItemDto.toItemDto(repository.getItem(id));
    }

    @Override
    public List<ItemDto> getAllUserItems(Long owner) {
        UserDto userDto = userService.get(owner);
        log.info("Найдены и возвращены все предметы пользователя с id = {}", owner);
        return repository.getAll().stream().filter(item -> item.getOwner().equals(owner)).map(ItemDto::toItemDto).toList();
    }

    @Override
    public ItemDto update(Long owner, Long itemId, Item newItem) {
        UserDto userDto = userService.get(owner);
        if (repository.getItem(itemId) == null) {
            log.error("Предмет с id = {} не найден!", itemId);
            throw new NotFoundException(String.format("Предмет с id = %d не найден!", itemId));
        }
        Item item = repository.getItem(itemId);
        if (!item.getOwner().equals(owner)) {
            log.error("Изменить вещь c id = {} может только владелец c id = {} ", itemId, owner);
            throw new ValidationException(String.format("Изменить вещь c id = %d может только владелец c id = %d ", itemId, owner));
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
        return ItemDto.toItemDto(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            log.info("Получена пустая строка запроса");
            return new ArrayList<>();
        }
        log.info("Найдены и возвращены все предметы с текстом = {}", text);
        return repository.getAll().stream().filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()) || item.getName().toLowerCase().contains(text.toLowerCase())).filter(Item::getAvailable).map(ItemDto::toItemDto).toList();
    }

    @Override
    public void delete(Long owner, Long itemId) {
        UserDto ownerDto = userService.get(owner);
        if (repository.getItem(itemId) == null) {
            log.error("Предмет с id = {} не найден!", itemId);
            throw new NotFoundException(String.format("Предмет с id = %d не найден!", itemId));
        }
        Item item = repository.getItem(itemId);
        if (!item.getOwner().equals(owner)) {
            log.error("Удалить вещь c id = {} может только владелец c id = {} ", itemId, owner);
            throw new ValidationException(String.format("Удалить вещь c id = %d может только владелец c id = %d ", itemId, owner));
        }
        log.info("Предмет с id = {} был удален", itemId);
        repository.delete(itemId);
    }
}
