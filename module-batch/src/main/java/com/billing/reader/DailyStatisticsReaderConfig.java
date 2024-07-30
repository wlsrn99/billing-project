package com.billing.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.billing.entity.VideoStatistic;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DailyStatisticsReaderConfig {

	private final DataSource dataSource;

	@Bean
	@StepScope
	public JdbcPagingItemReader<VideoStatistic> watchHistoryReader(
		@Value("#{stepExecutionContext['startVideoId']}") Long startVideoId,
		@Value("#{stepExecutionContext['endVideoId']}") Long endVideoId,
		@Value("#{jobParameters['date']}") LocalDate date,
		@Value("#{jobParameters['chunkSize']}") Integer chunkSize) throws Exception {

		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("startVideoId", startVideoId);
		parameterValues.put("endVideoId", endVideoId);

		String partitionDate = date.format(DateTimeFormatter.BASIC_ISO_DATE);

		try {
			return new JdbcPagingItemReaderBuilder<VideoStatistic>()
				.name("watchHistoryReader")
				.dataSource(dataSource)
				.queryProvider(createWatchHistoryQueryProvider(partitionDate))
				.parameterValues(parameterValues)
				.pageSize(chunkSize)
				.rowMapper((rs, rowNum) -> new VideoStatistic(
					rs.getLong("video_id"),
					date,
					rs.getLong("daily_view_count"),
					rs.getLong("daily_ad_view_count"),
					rs.getLong("daily_duration")
				))
				.build();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create watchHistoryReader", e);
		}
	}

	private PagingQueryProvider createWatchHistoryQueryProvider(String partitionDate) throws Exception {
		SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
		factory.setDataSource(dataSource);
		factory.setSelectClause("SELECT /*+ INDEX(w idx_watch_history_video_id) */ " +
			"w.video_id, " +
			"COUNT(DISTINCT w.id) as daily_view_count, " +
			"SUM(w.ad_view_count) as daily_ad_view_count, " +
			"SUM(w.duration) as daily_duration");
		factory.setFromClause("FROM watch_history PARTITION(p" + partitionDate + ") w");
		factory.setWhereClause("WHERE w.video_id BETWEEN :startVideoId AND :endVideoId");
		factory.setGroupClause("GROUP BY w.video_id");
		factory.setSortKey("video_id");

		return factory.getObject();
	}

	@Bean
	@StepScope
	public JdbcPagingItemReader<VideoStatistic> videoStatisticsReader(
		@Value("#{stepExecutionContext['startVideoId']}") Long startVideoId,
		@Value("#{stepExecutionContext['endVideoId']}") Long endVideoId,
		@Value("#{jobParameters['date']}") LocalDate date,
		@Value("#{jobParameters['chunkSize']}") Integer chunkSize) throws Exception {

		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("date", date);
		parameterValues.put("startVideoId", startVideoId);
		parameterValues.put("endVideoId", endVideoId);

		return new JdbcPagingItemReaderBuilder<VideoStatistic>()
			.name("videoStatisticsReader")
			.dataSource(dataSource)
			.queryProvider(createVideoStatisticsQueryProvider())
			.parameterValues(parameterValues)
			.pageSize(chunkSize)
			.rowMapper((rs, rowNum) -> new VideoStatistic(
				rs.getLong("video_id"),
				rs.getDate("date").toLocalDate(),
				rs.getLong("daily_view_count"),
				rs.getLong("daily_ad_view_count"),
				rs.getLong("daily_duration")
			))
			.build();
	}

	private PagingQueryProvider createVideoStatisticsQueryProvider() throws Exception {
		SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
		factory.setDataSource(dataSource);
		factory.setSelectClause("SELECT /*+ INDEX(vs idx_watch_history_video_id) */ video_id, date, daily_view_count, daily_ad_view_count, daily_duration");
		factory.setFromClause("FROM video_statistics vs");
		factory.setWhereClause("WHERE date = :date AND video_id BETWEEN :startVideoId AND :endVideoId");
		factory.setSortKey("video_id");

		return factory.getObject();
	}
}