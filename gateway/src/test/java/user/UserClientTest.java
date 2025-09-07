package user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.model.User;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserClient userClient;


    private User testUser = new User();

    @Test
    void get_CallBaseClientGet() {
        Long userId = 1L;

        ResponseEntity<Object> fakeResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        )).thenReturn(fakeResponse);

        // when
        userClient.get(userId);

        // then
        verify(restTemplate).exchange(
                contains("/1"),  // проверяем, что URL содержит ID
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void create_CallBaseClientPost() {

        ResponseEntity<Object> fakeResponse = new ResponseEntity<>(testUser, OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(fakeResponse);

        // when
        userClient.create(testUser);


        verify(restTemplate).exchange(
                contains(""),
                eq(HttpMethod.POST),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void update_CallBaseClientPatch() {
        ResponseEntity<Object> fakeResponse = new ResponseEntity<>(testUser, OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(fakeResponse);

        userClient.update(1L, testUser);

        verify(restTemplate).exchange(
                contains("/1"),
                eq(HttpMethod.PATCH),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void delete_CallBaseClientDelete() {
        ResponseEntity<Object> fakeResponse = new ResponseEntity<>(testUser, OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(),
                eq(Object.class)
        )).thenReturn(fakeResponse);

        userClient.delete(1L);

        verify(restTemplate).exchange(
                contains("/1"),
                eq(HttpMethod.DELETE),
                any(),
                eq(Object.class)
        );
    }


}
