package com.billing.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Collections;

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
		@Value("#{jobParameters['date']}") LocalDate date,
		@Value("#{jobParameters['chunkSize']}") int chunkSize) {
		JpaPagingItemReader<VideoStatistic> reader = new JpaPagingItemReader<>();
		reader.setQueryString(
			"SELECT new com.billing.entity.VideoStatistic(w.videoId, w.createdAt, COUNT(w.id), SUM(w.adViewCount), SUM(w.duration)) " +
				"FROM WatchHistory w " +
				"WHERE w.createdAt = :date " +
				"GROUP BY w.videoId, w.createdAt"
		);
		reader.setParameterValues(Collections.singletonMap("date", date));
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setPageSize(chunkSize); // jobParameters로부터 가져온 chunk 크기로 설정
		return reader;
	}

	@Bean
	@StepScope
	public JpaPagingItemReader<VideoStatistic> videoStatisticsReader(
		@Value("#{jobParameters['date']}") LocalDate date,
		@Value("#{jobParameters['chunkSize']}") int chunkSize) {
		return new JpaPagingItemReaderBuilder<VideoStatistic>()
			.name("videoStatisticsReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT vs FROM VideoStatistic vs WHERE vs.date = :date")
			.parameterValues(Collections.singletonMap("date", date))
			.pageSize(chunkSize)
			.saveState(false) // 멀티스레드 환경에서 안전하게 동작하도록 상태 저장 비활성화
			.build();
	}
}
