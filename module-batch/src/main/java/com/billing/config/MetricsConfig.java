package com.billing.config;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;

@Configuration
public class MetricsConfig {

	@Bean
	MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(@Value("${spring.application.name}") String appName) {
		return registry -> registry.config().commonTags("application", appName, "instance", UUID.randomUUID().toString());
	}

	@Bean
	public MeterBinder threadPoolTaskExecutorMetrics(ThreadPoolTaskExecutor taskExecutor) {
		return new ExecutorServiceMetrics(taskExecutor.getThreadPoolExecutor(),
			"billingThreadPool", Tags.empty());
	}
}
