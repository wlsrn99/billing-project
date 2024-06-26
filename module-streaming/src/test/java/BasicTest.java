import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.streaming.StremingProjectApplication;

@SpringBootTest(classes = StremingProjectApplication.class)
@ActiveProfiles("test")
public class BasicTest {
	@Test
	void contextLoads() {

	}
}
