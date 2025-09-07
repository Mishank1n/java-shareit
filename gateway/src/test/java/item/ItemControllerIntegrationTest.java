package item;

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
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItAppGateway.class)
@AutoConfigureMockMvc
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private Item testItem;
    private ResponseEntity<Object> responseEntity;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setAvailable(true);

        responseEntity = new ResponseEntity<>(testItem, HttpStatus.OK);
    }

    @Test
    @SneakyThrows
    void create_ShouldReturnCreatedItem() {
        when(itemClient.create(anyLong(), any(Item.class))).thenReturn(new ResponseEntity<>(testItem, HttpStatus.CREATED));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemClient, times(1)).create(1L, testItem);
    }

    @Test
    @SneakyThrows
    void get_ShouldReturnItem() {
        when(itemClient.get(1L, 1L)).thenReturn(responseEntity);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemClient, times(1)).get(1L, 1L);
    }

    @Test
    @SneakyThrows
    void update_ShouldReturnUpdatedItem() {
        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Item");
        updatedItem.setDescription("Updated Description");
        updatedItem.setAvailable(false);

        when(itemClient.update(1L, 1L, updatedItem)).thenReturn(new ResponseEntity<>(updatedItem, HttpStatus.OK));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemClient, times(1)).update(1L, 1L, updatedItem);
    }

    @Test
    @SneakyThrows
    void getAllUserItems_ShouldReturnList() {
        when(itemClient.getAllUserItems(1L)).thenReturn(new ResponseEntity<>(new Item[]{testItem}, HttpStatus.OK));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(itemClient, times(1)).getAllUserItems(1L);
    }

    @Test
    @SneakyThrows
    void search_ShouldReturnList() {
        when(itemClient.search("Test")).thenReturn(new ResponseEntity<>(new Item[]{testItem}, HttpStatus.OK));

        mockMvc.perform(get("/items/search")
                        .param("text", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(itemClient, times(1)).search("Test");
    }

    @Test
    @SneakyThrows
    void deleteItem_ShouldReturnOk() {
        when(itemClient.delete(1L, 1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).delete(1L, 1L);
    }

    @Test
    @SneakyThrows
    void postComment_ShouldReturnCreatedComment() {
        Comment comment = new Comment();
        comment.setText("Nice item!");
        ResponseEntity<Object> commentResponse = new ResponseEntity<>(comment, HttpStatus.CREATED);

        when(itemClient.postComment(1L, 1L, comment)).thenReturn(commentResponse);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Nice item!"));

        verify(itemClient, times(1)).postComment(1L, 1L, comment);
    }
}
