package com.billing.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j(topic = "사용자의 이메일을 요청 헤더에 추가")
@Component
public class UserContextFilter extends AbstractGatewayFilterFactory<UserContextFilter.Config> {

	public UserContextFilter() {
		super(Config.class);
	}

	public static class Config {
		// 필요한 경우 설정 속성을 여기에 추가
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
			.map(securityContext -> {
				addUserEmailToHeader(exchange, securityContext.getAuthentication());
				return exchange;
			})
			.onErrorResume(e -> {
				log.error("Error retrieving security context: {}", e.getMessage(), e);
				return Mono.just(exchange);
			})
			.flatMap(chain::filter);  // Proceed with the modified or original exchange
	}

	private void addUserEmailToHeader(ServerWebExchange exchange, Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String email = userDetails.getUsername();
			// Add the email to the request header if not already present
			if (!exchange.getRequest().getHeaders().containsKey("User-Email")) {
				exchange.getRequest().mutate().header("User-Email", email).build();
				log.info("Added User-Email header: {}", email);
			}
		}
	}
}
