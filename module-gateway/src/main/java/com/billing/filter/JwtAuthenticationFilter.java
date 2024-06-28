package com.billing.filter;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.billing.util.JwtUtil;
import com.billing.util.UserDetailServiceImpl;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
	private final JwtUtil jwtUtil;
	private final UserDetailServiceImpl userDetailsService;


	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		// 로그인과 관련된 경로는 필터링하지 않음
		String path = exchange.getRequest().getURI().getPath();
		if (path.startsWith("/users/")) {
			return chain.filter(exchange);
		}
		String accessToken = jwtUtil.getJwtFromHeader(exchange.getRequest());
		if (StringUtils.hasText(accessToken)) {
			try {
				Claims claims = jwtUtil.getClaimsFromToken(accessToken);

				if (claims.getExpiration().before(new Date())) {
					return requestNewAccessToken(exchange).flatMap(newAccessToken -> {
						if (newAccessToken != null) {
							String email = jwtUtil.getClaimsFromToken(newAccessToken).getSubject();
							return setAuthentication(email).then(chain.filter(exchange));
						} else {
							return handleTokenExpirationError(exchange);
						}
					});
				} else {
					return setAuthentication(accessToken).then(chain.filter(exchange));
				}

			} catch (Exception e) {
				log.error(e.getMessage());
				return handleUnauthorizedError(exchange);
			}
		}
		return chain.filter(exchange);
	}

	private Mono<String> requestNewAccessToken(ServerWebExchange exchange) {
		return WebClient.create()
			.post()
			// 토큰 재발급 요청
			.uri("http://localhost:8080/user/api/refresh")
			.header(HttpHeaders.AUTHORIZATION, jwtUtil.BEAR + jwtUtil.getJwtFromHeader(exchange.getRequest()))
			.retrieve()
			.bodyToMono(String.class)
			.onErrorResume(e -> {
				log.error(e.getMessage());
				return Mono.empty();
			});
	}

	private Mono<Void> setAuthentication(String email) {
		return createAuthentication(email)
			.flatMap(authentication ->
				Mono.defer(() -> {
					ReactiveSecurityContextHolder.withAuthentication(authentication);
					return Mono.empty();
				})
			);
	}


	private Mono<Authentication> createAuthentication(String email) {
		return userDetailsService.findByUsername(email)
			.map(userDetails ->
				new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities())
			);
	}


	private Mono<Void> handleTokenExpirationError(ServerWebExchange exchange) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		DataBufferFactory bufferFactory = response.bufferFactory();
		DataBuffer dataBuffer = bufferFactory.wrap("{\"error\": \"리프레쉬 토큰 만료\"}".getBytes(StandardCharsets.UTF_8));

		return response.writeWith(Mono.just(dataBuffer));
	}


	private Mono<Void> handleUnauthorizedError(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

		DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
		DataBuffer dataBuffer = bufferFactory.wrap("{\"error\": \"유효하지 않은 액세스 토큰.\"}".getBytes(StandardCharsets.UTF_8));

		return exchange.getResponse().writeWith(Mono.just(dataBuffer));
	}

}
