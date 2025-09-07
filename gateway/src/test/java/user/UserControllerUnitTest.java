package user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private ResponseEntity<Object> responseEntity;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@user.com");

        responseEntity = new ResponseEntity<>(testUser, HttpStatus.OK);
    }

    @Test
    void get_CallClientGet() {
        when(userClient.get(1L)).thenReturn(responseEntity);

        ResponseEntity<Object> response = userController.get(1L);

        verify(userClient, times(1)).get(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseEntity, response);
    }

    @Test
    void get_ThrowNotFoundException() {
        when(userClient.get(anyLong())).thenThrow(new NotFoundException("Пользователя не существует"));

        assertThrows(NotFoundException.class, () -> userController.get(1L));

    }

    @Test
    void create_CallClientCreate() {
        when(userClient.create(testUser)).thenReturn(responseEntity);

        ResponseEntity<Object> response = userController.create(testUser);

        verify(userClient, times(1)).create(testUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseEntity.getBody(), response.getBody());
    }
}