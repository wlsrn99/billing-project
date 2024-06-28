package com.billing.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
public class UserContextFilter extends AbstractGatewayFilterFactory<UserContextFilter.Config> {
	public UserContextFilter() {
		super(Config.class);
	}

	public static class Config {
		// Put the configuration properties here if needed
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			return ReactiveSecurityContextHolder.getContext()
				.map(securityContext -> {
					// Authentication 객체에서 UserDetails를 가져옵니다.
					Authentication authentication = securityContext.getAuthentication();
					if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
						UserDetails userDetails = (UserDetails) authentication.getPrincipal();
						String email = userDetails.getUsername();
						// Add the email to the request header
						exchange.getRequest().mutate().header("X-User-Email", email).build();
					}
					return exchange;
				})
				.defaultIfEmpty(exchange)
				.flatMap(chain::filter);  // Proceed with the modified or original exchange
		};
	}
}
