package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.model.User;

@Service
public class UserClient extends BaseClient {

    public static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Qualifier("userRestTemplate") RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> get(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> create(User user) {
        return post("", user);
    }

    public ResponseEntity<Object> update(Long userId, User newUser) {
        return patch("/" + userId, newUser);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete("/" + userId);
    }

}
