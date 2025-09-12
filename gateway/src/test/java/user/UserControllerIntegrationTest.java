package user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItAppGateway;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ThingIsAlreadyContain;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.model.User;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItAppGateway.class)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

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
    void get_ShouldReturnUser() throws Exception {
        when(userClient.get(1L)).thenReturn(responseEntity);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@user.com"));

        verify(userClient, times(1)).get(1L);
    }

    @SneakyThrows
    @Test
    void get_ThrowNotFoundException() {
        when(userClient.get(anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));

        verify(userClient, times(1)).get(1L);
    }

    @SneakyThrows
    @Test
    void create_ShouldReturnNewUser() {
        when(userClient.create(testUser)).thenReturn(responseEntity);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@user.com"));
    }

    @SneakyThrows
    @Test
    void create_ThrowMethodArgumentNotValidException() {
        testUser.setEmail("test");
        when(userClient.create(testUser)).thenReturn(responseEntity);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Адрес электронной почты должен быть реальным!"));
    }

    @Test
    @SneakyThrows
    void update_ShouldReturnUpdatedUser() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("New Test");
        newUser.setEmail("newTest@user.com");
        when(userClient.update(1L, newUser)).thenReturn(new ResponseEntity<>(newUser, HttpStatus.OK));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Test"))
                .andExpect(jsonPath("$.email").value("newTest@user.com"));

        verify(userClient, times(1)).update(1L, newUser);
    }

    @Test
    @SneakyThrows
    void update_ThrowThingIsAlreadyContain() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("New Test");
        newUser.setEmail("test@user.com");
        when(userClient.update(1L, newUser)).thenThrow(new ThingIsAlreadyContain(String.format("Пользователь с адресом электронной почты = %s уже существует", newUser.getEmail())));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value(String.format("Пользователь с адресом электронной почты = %s уже существует", newUser.getEmail())));

        verify(userClient, times(1)).update(1L, newUser);
    }

    @Test
    @SneakyThrows
    void delete_StatusIsOk() {
        when(userClient.delete(1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(delete("/users/1")
                        .accept("/"))
                .andExpect(status().isOk());

        verify(userClient, times(1)).delete(1L);
    }

}
