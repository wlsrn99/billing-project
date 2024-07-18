package com.billing.writer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.billing.entity.VideoStatistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyStatisticWriter implements ItemWriter<VideoStatistic> {
	private final JdbcTemplate jdbcTemplate;

	@Value("${batch.chunkSize:1000}")
	private int batchSize;

	@Override
	public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
		List<VideoStatistic> items = new ArrayList<>(chunk.getItems());
		int totalItems = items.size();

		for (int i = 0; i < totalItems; i += batchSize) {
			int endIndex = Math.min(i + batchSize, totalItems);
			List<VideoStatistic> batchItems = items.subList(i, endIndex);
			try {
				executeBulkInsert(batchItems);
			} catch (Exception e) {
				log.error("Error inserting batch: " + e.getMessage(), e);
				// 개별 레코드 삽입 시도
				for (VideoStatistic item : batchItems) {
					try {
						insertSingleItem(item);
					} catch (Exception ex) {
						log.error("Error inserting item: " + ex.getMessage(), ex);
					}
				}
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void executeBulkInsert(List<VideoStatistic> batchItems) {
		String sql = "INSERT INTO video_statistics (video_id, date, daily_view_count, daily_ad_view_count, daily_duration) VALUES ";
		StringBuilder valuePlaceholder = new StringBuilder();
		List<Object> params = new ArrayList<>();

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

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void insertSingleItem(VideoStatistic item) {
		String sql = "INSERT INTO video_statistics (video_id, date, daily_view_count, daily_ad_view_count, daily_duration) VALUES (?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, item.getVideoId(), item.getDate(), item.getDailyViewCount(), item.getDailyAdViewCount(), item.getDailyDuration());
	}
}