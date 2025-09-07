package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;


@Service
public class ItemClient extends BaseClient {

    public static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Qualifier("itemRestTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Long userId, Item item) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> get(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> update(Long owner, Long itemId, Item newItem) {
        return patch("/" + itemId, owner, newItem);
    }

    public ResponseEntity<Object> getAllUserItems(Long owner) {
        return get("", owner);
    }

    public ResponseEntity<Object> search(String text) {
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> delete(Long owner, Long itemId) {
        return delete("/" + itemId, owner);
    }

    public ResponseEntity<Object> postComment(Long userId, Long itemId, Comment comment) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}
