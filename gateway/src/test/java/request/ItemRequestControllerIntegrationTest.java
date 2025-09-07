package request;

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
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItAppGateway.class)
@AutoConfigureMockMvc
public class ItemRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private ItemRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new ItemRequest();
        validRequest.setId(1L);
        validRequest.setDescription("Нужна дрель");
        validRequest.setCreated(LocalDateTime.now());
    }

    @Test
    @SneakyThrows
    void create_ShouldReturnCreatedRequest() {
        when(itemRequestClient.create(anyLong(), any(ItemRequest.class)))
                .thenReturn(new ResponseEntity<>(validRequest, HttpStatus.CREATED));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Нужна дрель"));

        verify(itemRequestClient, times(1)).create(1L, validRequest);
    }

    @Test
    @SneakyThrows
    void create_ShouldFailValidation_WhenDescriptionMissing() {
        ItemRequest invalidRequest = new ItemRequest();
        invalidRequest.setCreated(LocalDateTime.now());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getAllUserItemRequests_ShouldReturnList() {
        when(itemRequestClient.getAllUserRequestsAndOrderByCreatedDesc(1L))
                .thenReturn(ResponseEntity.ok(new ItemRequest[]{validRequest}));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));

        verify(itemRequestClient, times(1)).getAllUserRequestsAndOrderByCreatedDesc(1L);
    }

    @Test
    @SneakyThrows
    void getAllRequestsNotByUser_ShouldReturnList() {
        when(itemRequestClient.getAllRequestsNotByUserAndSortedByCreatedDesc(1L))
                .thenReturn(ResponseEntity.ok(new ItemRequest[]{validRequest}));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));

        verify(itemRequestClient, times(1)).getAllRequestsNotByUserAndSortedByCreatedDesc(1L);
    }

    @Test
    @SneakyThrows
    void getByIdWithAnswers_ShouldReturnRequest() {
        when(itemRequestClient.getByIdWithAnswers(1L, 1L))
                .thenReturn(ResponseEntity.ok(validRequest));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Нужна дрель"));

        verify(itemRequestClient, times(1)).getByIdWithAnswers(1L, 1L);
    }
}
