import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItAppServer;

@SpringBootTest(classes = ShareItAppServer.class)
public class ShareItAppServerTest {

    @Test
    void checkStart() {
    }

    @Test
    void checkMain() {
        ShareItAppServer.main(new String[]{});
    }
}
