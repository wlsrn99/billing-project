package com.billing.config;

import java.time.LocalDate;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;

import com.billing.entity.VideoBill;
import com.billing.entity.VideoStatistic;
import com.billing.listener.MetricsStepExecutionListener;
import com.billing.listener.ThreadPoolMonitoringListener;
import com.billing.reader.DailyStatisticsReaderConfig;
import com.billing.validator.UniqueJobParametersValidator;
import com.partitioner.DailyBillingPartitioner;
import com.partitioner.DailyStatisticsPartitioner;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchConfig extends DefaultBatchConfiguration {
	private final DailyStatisticsReaderConfig readerConfig;

	@Override
	protected Isolation getIsolationLevelForCreate() {
		return Isolation.READ_COMMITTED;
	}

	@Override
	protected DataSource getDataSource() {
		return super.getDataSource();
	}

	@Override
	protected PlatformTransactionManager getTransactionManager() {
		return super.getTransactionManager();
	}

	@Bean
	@StepScope
	public DailyStatisticsPartitioner dailyStatisticsPartitioner(
		@Value("#{jobParameters['date']}") LocalDate date,
		@Value("${batch.gridSize:5}") int gridSize) {
		return new DailyStatisticsPartitioner(getDataSource(), gridSize, date);
	}

	@Bean
	@StepScope
	public DailyBillingPartitioner dailyBillingPartitioner(
		@Value("#{jobParameters['date']}") LocalDate date,
		@Value("${batch.gridSize:5}") int gridSize) {
		return new DailyBillingPartitioner(getDataSource(), gridSize, date);
	}

	@Bean
	public Job videoStatisticsJob(
		JobRepository jobRepository,
		Step partitionedDailyStatisticsStep,
		Step partitionedDailyBillingStep,
		ThreadPoolMonitoringListener threadPoolMonitoringListener
	) {
		return new JobBuilder("videoStatisticsJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(partitionedDailyStatisticsStep)
			.next(partitionedDailyBillingStep)
			.validator(new UniqueJobParametersValidator())
			.listener(threadPoolMonitoringListener)
			.build();
	}

	@Bean
	public Step partitionedDailyStatisticsStep(
		JobRepository jobRepository,
		Step dailyStatisticsSlaveStep,
		Partitioner dailyStatisticsPartitioner,
		@Value("${batch.gridSize:5}") int gridSize) {
		return new StepBuilder("partitionedDailyStatisticsStep", jobRepository)
			.partitioner("dailyStatisticsSlaveStep", dailyStatisticsPartitioner)
			.step(dailyStatisticsSlaveStep)
			.gridSize(gridSize)
			.taskExecutor(threadPoolTaskExecutor())
			.build();
	}

	@Bean
	public Step partitionedDailyBillingStep(
		JobRepository jobRepository,
		Step dailyBillingSlaveStep,
		Partitioner dailyBillingPartitioner,
		@Value("${batch.gridSize:5}") int gridSize) {
		return new StepBuilder("partitionedDailyBillingStep", jobRepository)
			.partitioner("dailyBillingSlaveStep", dailyBillingPartitioner)
			.step(dailyBillingSlaveStep)
			.gridSize(gridSize)
			.taskExecutor(threadPoolTaskExecutor())
			.build();
	}

	@Bean
	public Step dailyStatisticsSlaveStep(
		JobRepository jobRepository,
		JdbcPagingItemReader<VideoStatistic> watchHistoryReader,
		ItemProcessor<VideoStatistic, VideoStatistic> dailyStatisticsProcessor,
		ItemWriter<VideoStatistic> dailyStatisticWriter,
		MetricsStepExecutionListener metricsStepExecutionListener,
		@Value("${batch.chunkSize:1000}") int chunkSize
	) {
		return new StepBuilder("dailyStatisticsSlaveStep", jobRepository)
			.<VideoStatistic, VideoStatistic>chunk(chunkSize, getTransactionManager())
			.reader(watchHistoryReader)
			.processor(dailyStatisticsProcessor)
			.writer(dailyStatisticWriter)
			.listener(metricsStepExecutionListener)
			.build();
	}

	@Bean
	public Step dailyBillingSlaveStep(
		JobRepository jobRepository,
		JdbcPagingItemReader<VideoStatistic> videoStatisticsReader,
		ItemProcessor<VideoStatistic, VideoBill> dailyBillingProcessor,
		ItemWriter<VideoBill> dailyBillingWriter,
		MetricsStepExecutionListener metricsStepExecutionListener,
		@Value("${batch.chunkSize:1000}") int chunkSize
	) {
		return new StepBuilder("dailyBillingSlaveStep", jobRepository)
			.<VideoStatistic, VideoBill>chunk(chunkSize, getTransactionManager())
			.reader(videoStatisticsReader)
			.processor(dailyBillingProcessor)
			.writer(dailyBillingWriter)
			.listener(metricsStepExecutionListener)
			.build();
	}

	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(8); //m1칩의 cpu 수
		executor.setQueueCapacity(25);
		executor.setThreadNamePrefix("billing-thread-");
		executor.initialize();
		log.info("Initialized ThreadPoolTaskExecutor with core pool size: {}, max pool size: {}",
			executor.getCorePoolSize(), executor.getMaxPoolSize());
		return executor;
	}
}