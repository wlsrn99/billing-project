package com.streaming.aop;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {
	// 현재 스레드의 데이터 소스 타입을 결정하는 메소드입니다.
	// AbstractRoutingDataSource의 추상 메소드를 구현합니다.
	@Override
	protected Object determineCurrentLookupKey() {
		// DataSourceContextHolder에서 설정한 데이터 소스 타입을 반환합니다.
		return DataSourceContextHolder.getDataSourceType();
	}
}
