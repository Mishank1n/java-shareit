package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.model.ItemRequest;

@Service
public class ItemRequestClient extends BaseClient {

    public static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Qualifier("itemRequestRestTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Long userId, ItemRequest itemRequest) {
        return post("", userId, itemRequest);
    }

    public ResponseEntity<Object> getAllUserRequestsAndOrderByCreatedDesc(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequestsNotByUserAndSortedByCreatedDesc(Long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getByIdWithAnswers(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
