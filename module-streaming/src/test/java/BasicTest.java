import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.streaming.StremingModuleApplication;

@SpringBootTest(classes = StremingModuleApplication.class)
@ActiveProfiles("test")
public class BasicTest {
	@Test
	void contextLoads() {

	}
}
