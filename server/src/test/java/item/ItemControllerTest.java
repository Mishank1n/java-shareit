package item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItAppServer;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToOwner;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = ShareItAppServer.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private final String headerUserId = "X-Sharer-User-Id";

    @Test
    void createItem_Success() throws Exception {
        User testUser = new User(1L, "1", "test@user.com");
        Item item = new Item(null, "Drill", "Power drill", true, testUser, 1L, new ItemRequest());
        ItemDto itemDto = new ItemDto(1L, "Drill", "Power drill", true, testUser.getId());

        when(itemService.create(eq(1L), any(Item.class))).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Power drill"));
    }

    @Test
    void getItemById_Success() throws Exception {
        ItemDtoToOwner itemDtoToOwner = new ItemDtoToOwner(1L, "Drill", "Power drill", true, null, null, null, null);

        when(itemService.getById(1L, 1L)).thenReturn(itemDtoToOwner);

        mockMvc.perform(get("/items/1")
                        .header(headerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void getItemById_NotFound() throws Exception {
        when(itemService.getById(1L, 999L)).thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/999")
                        .header(headerUserId, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Item not found"));
    }

    @Test
    void updateItem_Success() throws Exception {
        User testUser = new User(1L, "1", "test@user.com");
        Item newItem = new Item(null, "Drill Updated", "Power drill updated", true, testUser, 1L, new ItemRequest());
        ItemDto itemDto = new ItemDto(1L, "Drill Updated", "Power drill updated", true, testUser.getId());

        when(itemService.update(eq(1L), eq(1L), any(Item.class))).thenReturn(newItem);

        mockMvc.perform(patch("/items/1")
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Drill Updated"));
    }

    @Test
    void deleteItem_Success() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header(headerUserId, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUserItems_Success() throws Exception {
        ItemDtoWithComments itemDtoWithComments = new ItemDtoWithComments(1L, "Drill", "Power drill", true, null, null);
        when(itemService.getAllUserItems(1L)).thenReturn(List.of(itemDtoWithComments));

        mockMvc.perform(get("/items")
                        .header(headerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void searchItems_Success() throws Exception {
        User testUser = new User(1L, "1", "test@user.com");
        Item item = new Item(null, "Drill", "Power drill", true, testUser, 1L, new ItemRequest());
        when(itemService.search("drill")).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void postComment_Success() throws Exception {
        Comment comment = new Comment(1L, "Great item!", null, null, null);
        CommentDto commentDto = new CommentDto(1L, "Great item!", "John", null);

        when(itemService.postComment(eq(1L), eq(1L), any(Comment.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("John"));
    }

}
