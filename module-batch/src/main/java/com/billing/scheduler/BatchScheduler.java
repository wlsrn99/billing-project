package com.billing.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class BatchScheduler {

	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;


	// @Scheduled(cron = "0/10 * * * * *") // 10초마다 실행
	// public void runTop5Job() {
	// 	try {
	// 		JobParameters jobParameters = new JobParametersBuilder()
	// 			.addLong("time", System.currentTimeMillis())
	// 			.toJobParameters();
	//
	// 		Job job = jobRegistry.getJob("top5VideosJob");
	// 		jobLauncher.run(job, jobParameters);
	// 	} catch (NoSuchJobException e) {
	// 		// 해당 이름의 Job이 없을 경우 예외 처리
	// 		e.printStackTrace();
	// 	} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
	// 		// Job 실행 중 발생할 수 있는 예외 처리
	// 		e.printStackTrace();
	// 	}
	// }

	@Scheduled(cron = "0/10 * * * * *") // 10초마다 실행
	public void runVideoStatisticJob() {
		try {
			log.info("Running video statistics job");
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();

			Job job = jobRegistry.getJob("videoStatisticsJob");
			jobLauncher.run(job, jobParameters);
		} catch (NoSuchJobException e) {
			// 해당 이름의 Job이 없을 경우 예외 처리
			e.printStackTrace();
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			// Job 실행 중 발생할 수 있는 예외 처리
			e.printStackTrace();
		}
	}


}