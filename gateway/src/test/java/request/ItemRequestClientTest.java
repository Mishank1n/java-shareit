package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ItemRequestClientTest {

    private RestTemplate restTemplate;
    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        itemRequestClient = new ItemRequestClient(restTemplate);
    }

    @Test
    void create_SendsPostWithHeaderAndBody() {
        Long userId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemRequestClient.create(userId, itemRequest);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), captor.capture(), eq(Object.class));

        HttpEntity captured = captor.getValue();
        assertEquals(itemRequest, captured.getBody());
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void getAllUserRequestsAndOrderByCreatedDesc_SendsGetWithHeader() {
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemRequestClient.getAllUserRequestsAndOrderByCreatedDesc(userId);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), captor.capture(), eq(Object.class));

        assertEquals("", urlCaptor.getValue()); // URL для пустого пути
        HttpEntity captured = captor.getValue();
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void getAllRequestsNotByUserAndSortedByCreatedDesc_SendsGetWithHeader() {
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemRequestClient.getAllRequestsNotByUserAndSortedByCreatedDesc(userId);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), captor.capture(), eq(Object.class));

        assertTrue(urlCaptor.getValue().endsWith("/all"));
        HttpEntity captured = captor.getValue();
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void getByIdWithAnswers_SendsGetWithHeader() {
        Long userId = 1L;
        Long requestId = 10L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemRequestClient.getByIdWithAnswers(userId, requestId);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), captor.capture(), eq(Object.class));

        assertTrue(urlCaptor.getValue().endsWith("/" + requestId));
        HttpEntity captured = captor.getValue();
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }
}
