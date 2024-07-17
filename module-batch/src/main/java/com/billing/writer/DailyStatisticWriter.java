package com.billing.writer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.billing.entity.VideoStatistic;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DailyStatisticWriter implements ItemWriter<VideoStatistic> {
	private final JdbcTemplate jdbcTemplate;

	@Value("${batch.chunkSize:1000}")
	private int batchSize;

	@Override
	@Transactional // 메서드 전체를 하나의 트랜잭션으로 처리
	public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
		// chunk의 모든 아이템을 새 ArrayList로 복사
		// 이는 불변성을 보장하고 안전한 서브리스트 연산을 위함
		List<VideoStatistic> items = new ArrayList<>(chunk.getItems());
		int totalItems = items.size();

		// 전체 아이템 리스트를 batchSize 크기의 작은 배치로 나누어 처리
		for (int i = 0; i < totalItems; i += batchSize) {
			// 현재 배치의 끝 인덱스 계산
			// Math.min 사용으로 마지막 배치가 batchSize보다 작을 수 있음을 보장
			int endIndex = Math.min(i + batchSize, totalItems);

			// 현재 배치에 해당하는 아이템들의 서브리스트 생성
			List<VideoStatistic> batchItems = items.subList(i, endIndex);

			// 현재 배치의 아이템들을 데이터베이스에 벌크 삽입
			executeBulkInsert(batchItems);
		}
	}

	/**
	 * 비디오 통계 데이터를 벌크 삽입하는 메서드
	 * @param batchItems 삽입할 VideoStatistic 객체들의 리스트
	 */
	protected void executeBulkInsert(List<VideoStatistic> batchItems) {
		String sql = "INSERT INTO video_statistics (video_id, date, daily_view_count, daily_ad_view_count, daily_duration) VALUES ";
		StringBuilder valuePlaceholder = new StringBuilder();
		List<Object> params = new ArrayList<>();

		// 각 VideoStatistic 객체에 대한 값 플레이스홀더와 파라미터 추가
		for (int i = 0; i < batchItems.size(); i++) {
			if (i > 0) {
				valuePlaceholder.append(", ");
			}
			valuePlaceholder.append("(?, ?, ?, ?, ?)");
			VideoStatistic stat = batchItems.get(i);
			params.add(stat.getVideoId());
			params.add(stat.getDate());
			params.add(stat.getDailyViewCount());
			params.add(stat.getDailyAdViewCount());
			params.add(stat.getDailyDuration());
		}

		sql += valuePlaceholder.toString();
		jdbcTemplate.update(sql, params.toArray());
	}
}