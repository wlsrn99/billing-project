package com.billing.writer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.billing.entity.VideoStatistic;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DailyStatisticWriter implements ItemWriter<VideoStatistic> {
	private final JdbcTemplate jdbcTemplate;

	@Override
	@Transactional
	public void write(Chunk<? extends VideoStatistic> chunk) {
		String sql = "INSERT INTO video_statistics (video_id, date, daily_view_count, daily_ad_view_count, daily_duration) " +
			"VALUES (?, ?, ?, ?, ?) " +
			"ON DUPLICATE KEY UPDATE " +
			"daily_view_count = VALUES(daily_view_count), " +
			"daily_ad_view_count = VALUES(daily_ad_view_count), " +
			"daily_duration = VALUES(daily_duration)";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				VideoStatistic stat = chunk.getItems().get(i);
				ps.setLong(1, stat.getVideoId());
				ps.setDate(2, java.sql.Date.valueOf(stat.getDate()));
				ps.setLong(3, stat.getDailyViewCount());
				ps.setLong(4, stat.getDailyAdViewCount());
				ps.setLong(5, stat.getDailyDuration());
			}

			@Override
			public int getBatchSize() {
				return chunk.getItems().size();
			}
		});
	}
}