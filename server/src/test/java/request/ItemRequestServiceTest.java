package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItAppServer;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@ContextConfiguration(classes = ShareItAppServer.class)
class ItemRequestServiceTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userService.create(new User(null, "John", "john@example.com"));
        user2 = userService.create(new User(null, "Alice", "alice@example.com"));
    }

    @Test
    void createRequest_Success() {
        ItemRequest request = new ItemRequest();
        request.setDescription("Need a drill");

        ItemRequest savedRequest = itemRequestService.create(user1.getId(), request);

        assertNotNull(savedRequest.getId());
        assertEquals("Need a drill", savedRequest.getDescription());
        assertEquals(user1.getId(), savedRequest.getRequestor().getId());
        assertNotNull(savedRequest.getCreated());
    }

    @Test
    void getAllUserRequestsAndOrderByCreatedDesc_Success() {
        ItemRequest request1 = itemRequestService.create(user1.getId(), new ItemRequest(null, "Request 1", user1, LocalDateTime.now()));
        ItemRequest request2 = itemRequestService.create(user1.getId(), new ItemRequest(null, "Request 2", user1, LocalDateTime.now().plusMinutes(1)));

        List<ItemRequestDtoWithAnswers> requests = itemRequestService.getAllUserRequestsAndOrderByCreatedDesc(user1.getId());

        assertEquals(2, requests.size());
        assertEquals("Request 2", requests.get(0).getDescription()); // Последний созданный первым в списке
    }

    @Test
    void getAllRequestsNotByUserAndSortedByCreatedDesc_Success() {
        itemRequestService.create(user1.getId(), new ItemRequest(null, "Request 1", user1, LocalDateTime.now()));
        itemRequestService.create(user2.getId(), new ItemRequest(null, "Request 2", user2, LocalDateTime.now().plusMinutes(1)));

        List<ItemRequestDtoWithoutAnswers> requests = itemRequestService.getAllRequestsNotByUserAndSortedByCreatedDesc(user1.getId());

        assertEquals(1, requests.size());
        assertEquals("Request 2", requests.get(0).getDescription());
    }

    @Test
    void getByIdWithAnswers_Success() {
        ItemRequest request = itemRequestService.create(user1.getId(), new ItemRequest(null, "Request 1", user1, LocalDateTime.now()));

        ItemRequestDtoWithAnswers found = itemRequestService.getByIdWithAnswers(user1.getId(), request.getId());

        assertEquals(request.getId(), found.getId());
        assertEquals("Request 1", found.getDescription());
    }

    @Test
    void getByIdWithAnswers_NotFound() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemRequestService.getByIdWithAnswers(user1.getId(), 999L));

        assertTrue(ex.getMessage().contains("не найден"));
    }
}
