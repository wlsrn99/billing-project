package com.billing.config;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.billing.entity.VideoBill;
import com.billing.entity.VideoStatistic;
import com.billing.listener.StatisticJobListener;
import com.billing.listener.ThreadPoolMonitoringListener;
import com.billing.validator.UniqueJobParametersValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
@Slf4j
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
	 *
	 * @return
	 */
	@Bean
	public Job videoStatisticsJob(
		Step dailyStatisticsStep,
		Step dailyBillingStep,
		ThreadPoolMonitoringListener threadPoolMonitoringListener) {
		return new JobBuilder("videoStatisticsJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(dailyStatisticsStep)
			.next(dailyBillingStep)
			.validator(new UniqueJobParametersValidator())
			.listener(threadPoolMonitoringListener)
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
		ItemWriter<VideoBill> dailyBillingWriter,
		TaskExecutor threadPoolTaskExecutor,
		StatisticJobListener statisticJobListener
	) {
		return new StepBuilder("dailyBillingStep", jobRepository)
			.<VideoStatistic, VideoBill>chunk(100, transactionManager)
			.reader(videoStatisticsReader)
			.processor(dailyBillingProcessor)
			.writer(dailyBillingWriter)
			.taskExecutor(threadPoolTaskExecutor)
			.listener(statisticJobListener)
			.listener(new StepExecutionListener() {
				// Job 파라미터를 확인하여 이 스텝이 이전에 실패한 스텝인지 확인
				@Override
				public void beforeStep(StepExecution stepExecution) {
					String failedStep = stepExecution.getJobParameters().getString("failedStep", "");
					if (!failedStep.isEmpty() && !failedStep.equals(stepExecution.getStepName())) {
						//스텝의 상태를 COMPLETED로 설정하여 재실행을 방지
						stepExecution.setStatus(BatchStatus.COMPLETED);
						stepExecution.setExitStatus(ExitStatus.COMPLETED);
					}
				}

				@Override
				public ExitStatus afterStep(StepExecution stepExecution) {
					return stepExecution.getExitStatus();
				}
			})
			.build();
	}

	@Bean
	public TaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		executor.setThreadNamePrefix("billing-thread-");
		executor.initialize();
		log.info("Initialized ThreadPoolTaskExecutor with core pool size: {}, max pool size: {}",
			executor.getCorePoolSize(), executor.getMaxPoolSize());
		return executor;
	}

}