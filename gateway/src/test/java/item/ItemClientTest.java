package item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ItemClient itemClient;

    @Test
    void create_SendsPostWithCorrectHeaderAndBody() {
        Long userId = 1L;
        Item item = new Item(); // твой объект Item
        ResponseEntity<Object> fakeResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(fakeResponse);

        // вызов метода
        itemClient.create(userId, item);

        // проверяем, что restTemplate вызван
        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), captor.capture(), eq(Object.class));

        HttpEntity captured = captor.getValue();

        // проверяем тело
        assertEquals(item, captured.getBody());

        // проверяем заголовок
        assertTrue(captured.getHeaders().containsKey("X-Sharer-User-Id"));
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void get_SendsGetWithHeader() {
        Long userId = 1L;
        Long itemId = 2L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemClient.get(userId, itemId);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), captor.capture(), eq(Object.class));

        assertTrue(urlCaptor.getValue().endsWith("/" + itemId));
        HttpEntity captured = captor.getValue();
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void update_SendsPatchWithHeaderAndBody() {
        Long userId = 1L;
        Long itemId = 2L;
        Item newItem = new Item();
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemClient.update(userId, itemId, newItem);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.PATCH), captor.capture(), eq(Object.class));

        assertTrue(urlCaptor.getValue().endsWith("/" + itemId));
        HttpEntity captured = captor.getValue();
        assertEquals(newItem, captured.getBody());
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void getAllUserItems_SendsGetWithHeader() {
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemClient.getAllUserItems(userId);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), captor.capture(), eq(Object.class));

        HttpEntity captured = captor.getValue();
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void search_SendsGetWithoutUserHeader() {
        String text = "test";
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemClient.search(text);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));

        assertTrue(urlCaptor.getValue().contains("/search?text=" + text));
    }

    @Test
    void delete_SendsDeleteWithHeader() {
        Long userId = 1L;
        Long itemId = 2L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemClient.delete(userId, itemId);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.DELETE), captor.capture(), eq(Object.class));

        assertTrue(urlCaptor.getValue().endsWith("/" + itemId));
        HttpEntity captured = captor.getValue();
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void postComment_SendsPostWithHeaderAndBody() {
        Long userId = 1L;
        Long itemId = 2L;
        Comment comment = new Comment();
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemClient.postComment(userId, itemId, comment);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), captor.capture(), eq(Object.class));

        assertTrue(urlCaptor.getValue().endsWith("/" + itemId + "/comment"));
        HttpEntity captured = captor.getValue();
        assertEquals(comment, captured.getBody());
        assertEquals(userId.toString(), captured.getHeaders().getFirst("X-Sharer-User-Id"));
    }

}
