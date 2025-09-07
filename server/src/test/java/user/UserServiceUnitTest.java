package user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ThingIsAlreadyContain;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceUnitTest {


    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository repository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = new User(1L, "John", "john@example.com");
        user2 = new User(2L, "Alice", "alice@example.com");
    }

    // ======== create ========
    @Test
    void createUser_Success() {
        when(repository.findAll()).thenReturn(List.of(user2));
        when(repository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        User created = userService.create(new User(null, "Bob", "bob@example.com"));

        assertNotNull(created.getId());
        assertEquals("Bob", created.getName());
        assertEquals("bob@example.com", created.getEmail());
    }

    @Test
    void createUser_EmailAlreadyExists() {
        when(repository.findAll()).thenReturn(List.of(user1));

        ThingIsAlreadyContain ex = assertThrows(ThingIsAlreadyContain.class,
                () -> userService.create(new User(null, "John Duplicate", "john@example.com")));

        assertTrue(ex.getMessage().contains("уже существует"));
    }

    // ======== get ========
    @Test
    void getUser_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(user1));

        User found = userService.get(1L);

        assertEquals("John", found.getName());
        assertEquals("john@example.com", found.getEmail());
    }

    @Test
    void getUser_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.get(999L));
        assertTrue(ex.getMessage().contains("не найден"));
    }

    // ======== update ========
    @Test
    void updateUser_Success_UpdateNameAndEmail() {
        User newUser = new User(null, "John Updated", "john.updated@example.com");

        when(repository.findById(1L)).thenReturn(Optional.of(user1));
        when(repository.existsByEmailAndIdNot("john.updated@example.com", 1L)).thenReturn(false);

        User updated = userService.update(1L, newUser);

        assertEquals("John Updated", updated.getName());
        assertEquals("john.updated@example.com", updated.getEmail());
    }

    @Test
    void updateUser_Success_Nothing() {
        User newUser = new User(null, "John Updated", "john.updated@example.com");

        when(repository.findById(1L)).thenReturn(Optional.of(newUser));
        when(repository.existsByEmailAndIdNot("john.updated@example.com", 1L)).thenReturn(false);

        User updated = userService.update(1L, newUser);

        assertEquals("John Updated", updated.getName());
        assertEquals("john.updated@example.com", updated.getEmail());
    }


    @Test
    void updateUser_EmailAlreadyExists() {
        User newUser = new User(null, "John", "alice@example.com");

        when(repository.existsByEmailAndIdNot("alice@example.com", 1L)).thenReturn(true);

        ThingIsAlreadyContain ex = assertThrows(ThingIsAlreadyContain.class,
                () -> userService.update(1L, newUser));

        assertTrue(ex.getMessage().contains("уже существует"));
    }

    @Test
    void updateUser_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        User newUser = new User(null, "Ghost", "ghost@example.com");

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.update(999L, newUser));
        assertTrue(ex.getMessage().contains("не найден"));
    }

    @Test
    void updateUser_PartialUpdate_NameOnly() {
        User newUser = new User(null, "Johnny", null);

        when(repository.findById(1L)).thenReturn(Optional.of(user1));
        when(repository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);

        User updated = userService.update(1L, newUser);

        assertEquals("Johnny", updated.getName());
        assertEquals("john@example.com", updated.getEmail());
    }

    @Test
    void updateUser_PartialUpdate_EmailOnly() {
        User newUser = new User(null, null, "johnny@example.com");

        when(repository.findById(1L)).thenReturn(Optional.of(user1));
        when(repository.existsByEmailAndIdNot("johnny@example.com", 1L)).thenReturn(false);

        User updated = userService.update(1L, newUser);

        assertEquals("John", updated.getName());
        assertEquals("johnny@example.com", updated.getEmail());
    }

    // ======== delete ========
    @Test
    void deleteUser_Success() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> userService.delete(1L));
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound() {
        when(repository.existsById(999L)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.delete(999L));
        assertTrue(ex.getMessage().contains("не найден"));
    }
}


