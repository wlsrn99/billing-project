package com.billing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.billing.filter.FailedAuthenticationEntryPoint;
import com.billing.filter.JwtAuthorizationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthorizationFilter jwtAuthorizationFilter;
	private final FailedAuthenticationEntryPoint authenticationEntryPoint;

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
			.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
			.authorizeExchange(exchanges -> exchanges
				.pathMatchers("/users/**").permitAll()
				.pathMatchers("/streamings/**").hasRole("USER")
				.anyExchange().authenticated())
			.addFilterAt(jwtAuthorizationFilter, SecurityWebFiltersOrder.AUTHENTICATION);

		// 예외처리 발생 시 반환 세팅
		http.exceptionHandling(exceptionHandling -> exceptionHandling
			.authenticationEntryPoint(authenticationEntryPoint));

		return http.build();
	}


	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
