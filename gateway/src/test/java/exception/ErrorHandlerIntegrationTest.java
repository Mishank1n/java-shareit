package exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItAppGateway;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ThingIsAlreadyContain;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.model.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItAppGateway.class)
@AutoConfigureMockMvc
public class ErrorHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void whenNotFoundException_thenReturns404() throws Exception {
        when(userClient.get(anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));
    }

    @Test
    void whenMissingUserIdHeader_thenReturns400() throws Exception {
        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Нельзя выполнить данный запрос без id пользователя от кого идет запрос"));
    }

    @Test
    void whenInvalidEmail_thenReturns400() throws Exception {
        User invalidUser = new User();
        invalidUser.setName("Test");
        invalidUser.setEmail("wrongEmail");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Адрес электронной почты должен быть реальным!"));
    }

    @Test
    void whenMissingRequestParam_thenReturns400() throws Exception {
        mockMvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Нельзя выполнить данный запрос без параметра запроса"));
    }

    @Test
    void whenValidationException_thenReturns400() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setName("Bad");
        user.setEmail("bad@user.com");

        when(userClient.create(any(User.class)))
                .thenThrow(new ValidationException("Ошибка валидации данных пользователя"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Ошибка валидации данных пользователя"));
    }

    @Test
    void whenThingIsAlreadyContain_thenReturns409() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@user.com");

        when(userClient.create(any(User.class)))
                .thenThrow(new ThingIsAlreadyContain(
                        String.format("Пользователь с адресом электронной почты = %s уже существует", user.getEmail())
                ));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Пользователь с адресом электронной почты = test@user.com уже существует"));
    }

    @Test
    void whenRuntimeException_thenReturns500() throws Exception {
        when(userClient.get(anyLong()))
                .thenThrow(new RuntimeException("Случайная ошибка"));

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Внутренняя ошибка сервера"));
    }
}
