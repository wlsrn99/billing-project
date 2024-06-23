package com.billing.billingproject.user.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.billing.billingproject.user.jwt.JwtAuthenticationFilter;
import com.billing.billingproject.user.jwt.JwtAuthorizationFilter;
import com.billing.billingproject.user.jwt.JwtUtil;
import com.billing.billingproject.user.repository.UserRepository;
import com.billing.billingproject.user.security.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final AuthenticationConfiguration authenticationConfiguration;
	private final UserRepository userRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userRepository);
		filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
		return filter;
	}

	@Bean
	public JwtAuthorizationFilter jwtAuthorizationFilter() {
		return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable());

		http.sessionManagement(sessionManagement ->
			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(requests ->
			requests
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/users/signup").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/users/test").permitAll()
				.anyRequest().authenticated()
		);

		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
		return http.build();
	}
}
