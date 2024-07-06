package com.billing.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;

import com.billing.exception.JobLogMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class StatisticJobListener implements JobExecutionListener, StepExecutionListener {

	private final JobRepository jobRepository;
	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
			JobLogMessage.CACHE_CLEARED.log(log);
		} else {
			JobLogMessage.JOB_INCOMPLETE.log(log);

			for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
				if (stepExecution.getExitStatus().equals(ExitStatus.FAILED)) {
					JobLogMessage.STEP_RESTARTING.log(log, stepExecution.getStepName());
					restartFailedStep(stepExecution);
				}
			}
		}
	}

	private void restartFailedStep(StepExecution failedStepExecution) {
		JobLogMessage.STEP_RESTARTING.log(log, failedStepExecution.getStepName());

		failedStepExecution.setStatus(BatchStatus.STOPPED);
		failedStepExecution.setExitStatus(ExitStatus.STOPPED);
		jobRepository.update(failedStepExecution);

		JobParameters jobParameters = new JobParametersBuilder()
			.addString("run.id", String.valueOf(System.currentTimeMillis()))
			.addString("restartedStepName", failedStepExecution.getStepName())
			.toJobParameters();

		try {
			Job job = jobRegistry.getJob("videoStatisticsJob");
			//재시작
			JobExecution restartedJobExecution = jobLauncher.run(job, jobParameters);
			log.info("Restarted job execution status: {}", restartedJobExecution.getStatus());
		} catch (NoSuchJobException e) {
			JobLogMessage.RESTART_FAILED.log(log, failedStepExecution.getStepName(), "Job not found: " + e.getMessage());
		} catch (Exception e) {
			JobLogMessage.RESTART_FAILED.log(log, failedStepExecution.getStepName(),e.getMessage());
		}
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		JobLogMessage.STEP_STARTING.log(log, stepExecution.getStepName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if (stepExecution.getStatus() == BatchStatus.FAILED) {
			JobLogMessage.STEP_FAILED.log(log, stepExecution.getStepName());
			return ExitStatus.FAILED;
		} else {
			JobLogMessage.STEP_COMPLETED.log(log, stepExecution.getStepName());
			return ExitStatus.COMPLETED;
		}
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		JobLogMessage.JOB_STARTING.log(log, jobExecution.getJobInstance().getJobName());
	}
}
