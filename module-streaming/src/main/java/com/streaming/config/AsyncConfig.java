package com.streaming.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

	/**
	 ThreadPoolTaskExecutor: Spring에서 제공하는 TaskExecutor의 구현체
	 CorePoolSize: 스레드 풀의 기본 크기를 설정
	 MaxPoolSize: 최대 동시 실행 스레드 -> 스레드 풀이 바쁘면 최대 설정한 스레드 개수까지 확장
	 QueueCapacity: 작업 큐의 용량으로, CorePoolSize를 초과하는 작업은 큐에 보관, 동시에 많은 요청이 들어왔을 때 일정 수의 요청을 대기시킬 수 있도록
	 */
	@Override
	@Bean(name = "taskExecutor")
	public Executor getAsyncExecutor(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		executor.setThreadNamePrefix("StreamingAsyncExecutor-");
		executor.initialize();
		return executor;
	}

}
