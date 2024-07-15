package com.billing.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.billing.entity.VideoStatistic;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DailyStatisticsReaderConfig {

	private final EntityManagerFactory entityManagerFactory;

	@Bean
	@StepScope
	public JpaPagingItemReader<VideoStatistic> watchHistoryReader(
		@Value("#{stepExecutionContext['startVideoId']}") Long startVideoId,
		@Value("#{stepExecutionContext['endVideoId']}") Long endVideoId,
		@Value("#{jobParameters['date']}") LocalDate date,
		@Value("#{jobParameters['chunkSize']}") Integer chunkSize) {
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("date", date);
		parameterValues.put("startVideoId", startVideoId);
		parameterValues.put("endVideoId", endVideoId);

		return new JpaPagingItemReaderBuilder<VideoStatistic>()
			.name("watchHistoryReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT new com.billing.entity.VideoStatistic(w.videoId, w.createdAt, COUNT(w.id), SUM(w.adViewCount), SUM(w.duration)) " +
				"FROM WatchHistory w " +
				"WHERE w.createdAt = :date " +
				"AND w.videoId BETWEEN :startVideoId AND :endVideoId " +
				"GROUP BY w.videoId, w.createdAt")
			.parameterValues(parameterValues)
			.pageSize(chunkSize)
			.saveState(false)
			.build();
	}

	@Bean
	@StepScope
	public JpaPagingItemReader<VideoStatistic> videoStatisticsReader(
		@Value("#{stepExecutionContext['startVideoId']}") Long startVideoId,
		@Value("#{stepExecutionContext['endVideoId']}") Long endVideoId,
		@Value("#{jobParameters['date']}") LocalDate date,
		@Value("#{jobParameters['chunkSize']}") Integer chunkSize) {
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("date", date);
		parameterValues.put("startVideoId", startVideoId);
		parameterValues.put("endVideoId", endVideoId);

		return new JpaPagingItemReaderBuilder<VideoStatistic>()
			.name("videoStatisticsReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT v FROM VideoStatistic v " +
				"WHERE v.date = :date " +
				"AND v.videoId BETWEEN :startVideoId AND :endVideoId")
			.parameterValues(parameterValues)
			.pageSize(chunkSize)
			.saveState(false)
			.build();
	}
}