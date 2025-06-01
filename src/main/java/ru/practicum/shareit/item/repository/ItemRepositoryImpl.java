package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Long, Item> repository = new HashMap<>();

    @Override
    public List<Item> getAll() {
        return repository.values().stream().toList();
    }

    @Override
    public Item getItem(Long id) {
        return repository.getOrDefault(id, null);
    }

    @Override
    public Item addItem(Item item) {
        item.setId(getNewId());
        repository.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Long id) {
        repository.remove(id);
    }

    private Long getNewId() {
        Long id = repository.keySet().stream().mapToLong(Long::longValue).max().orElse(0);
        return ++id;
    }
}
