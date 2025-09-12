package client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class BaseClientTest {

    private RestTemplate restTemplate;
    private TestBaseClient baseClient;

    static class TestBaseClient extends BaseClient {
        public TestBaseClient(RestTemplate restTemplate) {
            super(restTemplate);
        }

        @Override
        public ResponseEntity<Object> get(String path, Long userId, Map<String, Object> params) {
            return super.get(path, userId, params);
        }
    }

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        baseClient = new TestBaseClient(restTemplate);
    }

    @Test
    void testPrepareGatewayResponse_returnsResponseIf2xx() {
        ResponseEntity<Object> fakeResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap())
        ).thenReturn(fakeResponse);

        ResponseEntity<Object> response = baseClient.get("/items", 1L, new HashMap<>());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("success");
    }

    @Test
    void testPrepareGatewayResponse_errorWithBody() {
        ResponseEntity<Object> fakeResponse =
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error-body");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap())
        ).thenReturn(fakeResponse);

        ResponseEntity<Object> response = baseClient.get("/items", 1L, new HashMap<>());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("error-body");
    }

    @Test
    void testPrepareGatewayResponse_errorWithoutBody() {
        ResponseEntity<Object> fakeResponse =
                ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // без тела

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap())
        ).thenReturn(fakeResponse);

        ResponseEntity<Object> response = baseClient.get("/items", 1L, new HashMap<>());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.hasBody()).isFalse();
    }

    @Test
    void testPrepareGatewayResponse_errorThrownAsHttpStatusCodeException() {
        HttpStatusCodeException exception = new HttpStatusCodeException(HttpStatus.INTERNAL_SERVER_ERROR, "ISE") {
            @Override
            public byte[] getResponseBodyAsByteArray() {
                return "server-error".getBytes(StandardCharsets.UTF_8);
            }
        };

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap())
        ).thenThrow(exception);

        ResponseEntity<Object> response = baseClient.get("/items", 1L, new HashMap<>());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(new String((byte[]) response.getBody())).isEqualTo("server-error");
    }
}
