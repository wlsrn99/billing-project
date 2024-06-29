package com.billing.user.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.billing.user.jwt.JwtUtil;
import com.billing.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		//csrf 보호기능 비활성화
		http.csrf(AbstractHttpConfigurer::disable);

		// WWW-Authenticate 비활성화
		http.httpBasic(AbstractHttpConfigurer::disable);

		// 세션 방식 비활성화
		http.sessionManagement(
			sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// //경로별 인가
		// http.authorizeHttpRequests(
		// 	requests -> requests.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
		// 		.permitAll()
		// 		.requestMatchers(HttpMethod.POST, "/user/api/login")
		// 		.permitAll()
		// 		.requestMatchers(HttpMethod.POST, "/user/api/signup")
		// 		.permitAll()
		// 		.anyRequest()
		// 		.authenticated());

		//경로별 인가 작업
		http.authorizeHttpRequests((auth) -> auth
			.anyRequest().permitAll());

		return http.build();
	}
}
