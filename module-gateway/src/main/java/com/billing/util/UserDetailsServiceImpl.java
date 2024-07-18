package com.billing.util;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	private static final String AUTHENTICATION_SERVICE_URL = "user/api/{userEmail}";
	private final WebClient webClient;

	public UserDetailsServiceImpl() {
		this.webClient = WebClient.builder().baseUrl("http://localhost:8080/users").build();
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		try {
			// User 서비스의 사용자 정보 조회 API 호출
			Mono<UserDetails> userDetailsMono = webClient.get()
				.uri(AUTHENTICATION_SERVICE_URL, email)
				.retrieve()
				.bodyToMono(UserDetails.class)
				.onErrorMap(WebClientResponseException.class, ex -> {
					if (ex.getStatusCode().is4xxClientError()) {
						log.error("Client error while fetching user details for email: {}", email, ex);
						return new UsernameNotFoundException("User not found : " + email, ex);
					} else if (ex.getStatusCode().is5xxServerError()) {
						log.error("Server error while fetching user details for email: {}", email, ex);
						return new UsernameNotFoundException("Service unavailable : " + email, ex);
					}
					return ex;
				});

			UserDetails userDetails = userDetailsMono.block(); // 블로킹 방식으로 결과를 기다림
			if (userDetails == null) {
				throw new UsernameNotFoundException("User not found : " + email);
			}
			return userDetails;
		} catch (UsernameNotFoundException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while fetching user details for email: {}", email, e);
			throw new UsernameNotFoundException("Unexpected error : " + email, e);
		}
	}
}
