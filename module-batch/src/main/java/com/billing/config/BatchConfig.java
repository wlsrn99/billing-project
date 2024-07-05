package com.billing.config;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.billing.entity.VideoStatistic;
import com.billing.entity.WatchHistory;
import com.billing.listener.DailyStatisticJobListener;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class BatchConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final DailyStatisticJobListener dailyStatisticJobListener;



	@Bean
	public Job videoStatisticsJob(Step dailyStatisticsStep, Step dailyBillingStep) {
		return new JobBuilder("videoStatisticsJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(dailyStatisticsStep)
			.next(dailyBillingStep)
			.listener(dailyStatisticJobListener)
			.build();
	}

	@Bean
	public Step dailyStatisticsStep(
		JpaPagingItemReader<WatchHistory> watchHistoryReader,
		ItemProcessor<WatchHistory, VideoStatistic> dailyStatisticsProcessor,
		ItemWriter<VideoStatistic> dailyStatisticWriter
	) {
		return new StepBuilder("dailyStatisticsStep", jobRepository)
			.<WatchHistory, VideoStatistic>chunk(10, transactionManager)
			.reader(watchHistoryReader)
			.processor(dailyStatisticsProcessor)
			.writer(dailyStatisticWriter)
			.build();
	}

	@Bean
	public Step dailyBillingStep(
		ItemReader<VideoStatistic> dailyBillingReader,
		ItemProcessor<VideoStatistic, VideoStatistic> dailyBillingProcessor,
		ItemWriter<VideoStatistic> dailyBillingWriter
	){
		return new StepBuilder("dailyBillingStep", jobRepository)
			.<VideoStatistic, VideoStatistic>chunk(10, transactionManager)
			.reader(dailyBillingReader)
			.processor(dailyBillingProcessor)
			.writer(dailyBillingWriter)
			.build();
	}

}
