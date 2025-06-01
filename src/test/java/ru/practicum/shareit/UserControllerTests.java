package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTests {

    @Autowired
    private UserController controller;

    private User getUser() {
        User user = new User();
        user.setName("Test");
        user.setEmail("Test@test.ru");
        return user;
    }


    @Test
    public void checkCreateUserAndGetUser() {
        UserDto userDto = controller.create(getUser());
        assertEquals(userDto, controller.get(userDto.getId()));
        controller.delete(userDto.getId());
    }

    @Test
    public void checkDeleteUser() {
        UserDto userDto = controller.create(getUser());
        controller.delete(userDto.getId());
        assertThrows(NotFoundException.class, () -> {
            controller.get(userDto.getId());
        });
    }

    @Test
    public void checkUpdateUser() {
        UserDto userDto = controller.create(getUser());
        User newUser = new User();
        newUser.setName("NewTest");
        assertEquals("NewTest", controller.update(userDto.getId(), newUser).getName());
        controller.delete(userDto.getId());
    }

}
