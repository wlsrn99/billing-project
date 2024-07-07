package com.billing.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("user-service", r -> r
				.path("/users/**")
				.filters(f -> f.stripPrefix(1)) // 첫 번째 경로 세그먼트 (/users) 제거
				.uri("lb://USER-SERVICE")) // 유레카 서버 가리키기
			.route("streaming-service", r -> r
				.path("/streamings/**")
				.filters(f -> f.stripPrefix(1)) // 첫 번째 경로 세그먼트 제거
				.uri("lb://STREAMING-SERVICE")) // 유레카 서버 가리키기
			.build();
	}
}
