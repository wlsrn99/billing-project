import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.streaming.StremingModuleApplication;
import com.streaming.dto.bill.BillVideoDTO;
import com.streaming.dto.bill.PeriodType;
import com.streaming.service.BatchService;
import com.streaming.aop.DataSourceContextHolder;
import com.streaming.aop.DataSourceType;

@SpringBootTest(
	classes = StremingModuleApplication.class,
	properties = {
		"eureka.client.enabled=false"
	}
)
@ActiveProfiles("test")
public class AopTest {
	@Autowired
	private BatchService billService;

	@Test
	void testReadBill() {
		// 메소드 실행 전 데이터 소스 타입 확인
		DataSourceType beforeType = DataSourceContextHolder.getDataSourceType();

		// 메소드 실행
		List<BillVideoDTO> result = billService.readBill(1L, LocalDate.of(2024, 06, 06), PeriodType.MONTHLY);

		// 메소드 실행 후 데이터 소스 타입 확인
		DataSourceType afterType = DataSourceContextHolder.getDataSourceType();

		// 검증
		assertEquals(DataSourceType.READ, afterType, "데이터 소스 타입이 READ여야 합니다.");
		assertTrue(DataSourceContextHolder.isRead(), "읽기 전용 데이터 소스가 사용되어야 합니다.");

	}
}