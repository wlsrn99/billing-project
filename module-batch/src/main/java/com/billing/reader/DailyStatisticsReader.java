package com.billing.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Collections;

import com.billing.entity.WatchHistory;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class DailyStatisticsReader {

	@Bean
	@StepScope
	public JpaPagingItemReader<WatchHistory> watchHistoryReader(EntityManagerFactory entityManagerFactory) {
		JpaPagingItemReader<WatchHistory> reader = new JpaPagingItemReader<>();
		reader.setQueryString("SELECT w FROM WatchHistory w WHERE w.createdAt = :date");
		reader.setParameterValues(Collections.singletonMap("date", LocalDate.of(2024, 6, 6)));
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setPageSize(10); // chunk 크기로 설정
		return reader;
	}

}
