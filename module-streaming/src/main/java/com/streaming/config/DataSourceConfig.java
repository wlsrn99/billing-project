package com.streaming.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;

import com.streaming.aop.RoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import com.streaming.aop.DataSourceType;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAspectJAutoProxy
public class DataSourceConfig {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.write")
	public DataSource writeDataSource() {
		return DataSourceBuilder
			.create()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.read")
	public DataSource readDataSource() {
		return DataSourceBuilder
			.create()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean
	@Primary
	public DataSource routingDataSource() {
		RoutingDataSource routingDataSource = new RoutingDataSource();
		Map<Object, Object> dataSourceMap = new HashMap<>();
		dataSourceMap.put(DataSourceType.WRITE, writeDataSource());
		dataSourceMap.put(DataSourceType.READ, readDataSource());
		routingDataSource.setTargetDataSources(dataSourceMap);
		routingDataSource.setDefaultTargetDataSource(writeDataSource());
		return routingDataSource;
	}
}