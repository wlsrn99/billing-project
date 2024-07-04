package com.billing.config;


import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.billing.entity.DailyVideo;
import com.billing.entity.VideoStatistic;
import com.billing.processor.BillStatisticsProcessor;
import com.billing.processor.DailyStatisticsProcessor;
import com.billing.processor.MonthlyStatisticsProcessor;
import com.billing.processor.WeeklyStatisticsProcessor;
import com.billing.reader.DailyVideoReader;
import com.billing.repository.DailyVideoRepository;
import com.billing.repository.VideoRepository;
import com.billing.writer.VideoStatisticWriter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class BatchConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		JobRegistryBeanPostProcessor jobProcessor = new JobRegistryBeanPostProcessor();
		jobProcessor.setJobRegistry(jobRegistry);
		return jobProcessor;
	}

	@Bean
	public CompositeItemProcessor<DailyVideo, VideoStatistic> compositeProcessor(
		DailyVideoRepository dailyVideoRepository,
		VideoRepository videoRepository) {
		List<ItemProcessor<?, VideoStatistic>> processors = List.of(
			new DailyStatisticsProcessor(),
			new WeeklyStatisticsProcessor(dailyVideoRepository),
			new MonthlyStatisticsProcessor(dailyVideoRepository),
			new BillStatisticsProcessor(videoRepository)
		);

		CompositeItemProcessor<DailyVideo, VideoStatistic> compositeProcessor = new CompositeItemProcessor<>();
		compositeProcessor.setDelegates(processors);
		return compositeProcessor;
	}



	@Bean
	public Job videoStatisticsJob(Step calculateStatisticsStep) {
		return new JobBuilder("videoStatisticsJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(calculateStatisticsStep)
			.build();
	}

	@Bean
	public Step calculateStatisticsStep(DailyVideoReader dailyVideoReader,
		ItemProcessor<? super DailyVideo, ? extends VideoStatistic> compositeProcessor,
		VideoStatisticWriter videoStatisticWriter
		) {
		return new StepBuilder("calculateStatisticsStep", jobRepository)
			.<DailyVideo, VideoStatistic>chunk(10, transactionManager)
			.reader(dailyVideoReader)
			.processor(compositeProcessor)
			.writer(videoStatisticWriter)
			.build();
	}




}
