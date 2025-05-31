package ru.practicum.shareit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemControllerTests {

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController controller;

    @AfterEach
    public void afterEach() {
        userController.delete(1L);
    }

    private Item getItem() {
        Item item = new Item();
        item.setName("Test");
        item.setDescription("test");
        item.setAvailable(true);
        return item;
    }

    private User getOwner() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.ru");
        return user;
    }

    @Test
    public void checkCreateAndGetItem() {
        UserDto owner = userController.create(getOwner());
        ItemDto itemDto = controller.create(owner.getId(), getItem());
        assertEquals(itemDto, controller.get(itemDto.getId()));
        controller.deleteItem(owner.getId(), itemDto.getId());
    }

    @Test
    public void checkGetUserItems() {
        UserDto owner = userController.create(getOwner());
        ItemDto itemDto = controller.create(owner.getId(), getItem());
        User secondUser = new User();
        secondUser.setName("Second Test");
        secondUser.setEmail("SecondTest@test.ru");
        UserDto secondUserDto = userController.create(secondUser);
        assertEquals(1, controller.getAllUserItems(owner.getId()).size());
        assertEquals(0, controller.getAllUserItems(secondUserDto.getId()).size());
        controller.deleteItem(owner.getId(), itemDto.getId());
        userController.delete(secondUserDto.getId());
    }

    @Test
    public void checkUpdateItem() {
        UserDto owner = userController.create(getOwner());
        ItemDto itemDto = controller.create(owner.getId(), getItem());
        Item newItem = new Item();
        newItem.setName("Second Test");
        assertEquals("Second Test", controller.update(owner.getId(), itemDto.getId(), newItem).getName());
        controller.deleteItem(owner.getId(), itemDto.getId());
    }

    @Test
    public void checkSearchItem() {
        UserDto owner = userController.create(getOwner());
        ItemDto itemDto = controller.create(owner.getId(), getItem());
        assertEquals(itemDto, controller.search(itemDto.getName()).getFirst());
        controller.deleteItem(owner.getId(), itemDto.getId());
    }
}
