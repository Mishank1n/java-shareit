import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItAppGateway;


@SpringBootTest(classes = ShareItAppGateway.class)
public class ShareitAppGatewayTest {

    @Test
    void checkStart() {
    }

    @Test
    void checkMain() {
        ShareItAppGateway.main(new String[]{});
    }
}
