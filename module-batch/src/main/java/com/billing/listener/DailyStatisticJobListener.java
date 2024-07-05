package com.billing.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import com.billing.util.GlobalSingletonCache;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DailyStatisticJobListener implements JobExecutionListener {
	private final GlobalSingletonCache globalCache = GlobalSingletonCache.getInstance();

	@Override
	public void afterJob(JobExecution jobExecution) {
		globalCache.clearCache(); //잡이 끝나면 글로벌 캐시 비우기

		if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
			log.info("Cache cleared after job completion.");
		} else {
			log.error("Job did not complete successfully, but cache still cleared.");
		}
	}
}
