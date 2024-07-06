package com.billing.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.billing.entity.VideoBill;
import com.billing.entity.VideoStatistic;
import com.billing.listener.StatisticJobListener;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class BatchConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;

	@Bean
	public BeanPostProcessor jobRegistryBeanPostProcessor() {
		JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
		postProcessor.setJobRegistry(jobRegistry);
		return postProcessor;
	}


	@Bean
	public StatisticJobListener statisticJobListener() {
		return new StatisticJobListener(jobRepository, jobLauncher, jobRegistry);
	}

	/**
	 *
	 * @param dailyStatisticsStep 통계 데이터를 만드는 step
	 * @param dailyBillingStep 정산데이터를 만드는 step
	 * @param statisticJobListener job과 step에 대한 상태 체크, Job 실패 시 재시작 로직
	 * @return
	 */
	@Bean
	public Job videoStatisticsJob(Step dailyStatisticsStep, Step dailyBillingStep, StatisticJobListener statisticJobListener) {
		return new JobBuilder("videoStatisticsJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.listener(statisticJobListener)
			.start(dailyStatisticsStep)
			.next(dailyBillingStep)
			.build();
	}

	@Bean
	public Step dailyStatisticsStep(
		JpaPagingItemReader<VideoStatistic> watchHistoryReader,
		ItemProcessor<VideoStatistic, VideoStatistic> dailyStatisticsProcessor,
		ItemWriter<VideoStatistic> dailyStatisticWriter
	) {
		return new StepBuilder("dailyStatisticsStep", jobRepository)
			.<VideoStatistic, VideoStatistic>chunk(10, transactionManager)
			.reader(watchHistoryReader)
			.processor(dailyStatisticsProcessor)
			.writer(dailyStatisticWriter)
			.build();
	}

	@Bean
	public Step dailyBillingStep(
		JpaPagingItemReader<VideoStatistic> videoStatisticsReader,
		ItemProcessor<VideoStatistic, VideoBill> dailyBillingProcessor,
		ItemWriter<VideoBill> dailyBillingWriter
	) {
		return new StepBuilder("dailyBillingStep", jobRepository)
			.<VideoStatistic, VideoBill>chunk(10, transactionManager)
			.reader(videoStatisticsReader)
			.processor(dailyBillingProcessor)
			.writer(dailyBillingWriter)
			.build();
	}
}
