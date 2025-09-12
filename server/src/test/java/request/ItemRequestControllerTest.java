package request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@ContextConfiguration(classes = ShareItAppServer.class)
class ItemRequestControllerTest {

    private final String userIdHeader = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService service;

    @Test
    void createRequest_Success() throws Exception {
        ItemRequest itemRequest = new ItemRequest(null, "Need a drill", null, null);
        ItemRequest savedRequest = new ItemRequest(1L, "Need a drill", null, null); // <- id = 1

        when(service.create(eq(1L), any(ItemRequest.class))).thenReturn(savedRequest);

        mockMvc.perform(post("/requests")
                        .header(userIdHeader, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1)) // теперь id=1
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }


    @Test
    void createRequest_MissingHeader() throws Exception {
        ItemRequest itemRequest = new ItemRequest(null, "Need a drill", null, null);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Нельзя выполнить данный запрос без id пользователя от кого идет запрос"));
    }

    @Test
    void getAllUserRequests_Success() throws Exception {
        ItemRequestDtoWithAnswers dto = new ItemRequestDtoWithAnswers(1L, "Need a drill", null, null);
        when(service.getAllUserRequestsAndOrderByCreatedDesc(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a drill"));
    }

    @Test
    void getAllRequestsNotByUser_Success() throws Exception {
        ItemRequestDtoWithoutAnswers dto = new ItemRequestDtoWithoutAnswers(2L, "Need a hammer", null);
        when(service.getAllRequestsNotByUserAndSortedByCreatedDesc(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].description").value("Need a hammer"));
    }

    @Test
    void getRequestById_Success() throws Exception {
        ItemRequestDtoWithAnswers dto = new ItemRequestDtoWithAnswers(1L, "Need a drill", null, null);
        when(service.getByIdWithAnswers(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/requests/1")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getRequestById_NotFound() throws Exception {
        when(service.getByIdWithAnswers(1L, 999L)).thenThrow(new NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/999")
                        .header(userIdHeader, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Request not found"));
    }
}
