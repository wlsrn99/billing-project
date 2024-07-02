package com.billing.filter;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.billing.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements WebFilter {
	private final JwtUtil jwtUtil;


	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		// 로그인과 관련된 경로는 필터링하지 않음
		String path = exchange.getRequest().getURI().getPath();
		if (path.startsWith("/users/")) {
			return chain.filter(exchange);
		}
		String accessToken = jwtUtil.resolveToken(exchange.getRequest());
		if (StringUtils.hasText(accessToken)) {
			try {
				if(jwtUtil.validateToken(accessToken)) {
					log.info("토큰이 유효, 기존 토큰을 사용");
					jwtUtil.setAuthentication(accessToken);
					return mutateExchange(exchange, chain, accessToken);
				}
				else {
					log.info("토큰이 만료, 새로운 토큰을 요청");
					Mono<String> result = requestNewAccessToken(exchange);
					return result.flatMap(newAccessToken -> {
						if(newAccessToken != null) {
							jwtUtil.setAuthentication(newAccessToken);
							return mutateExchange(exchange, chain, newAccessToken);
						}
						else{
							return handleTokenError(exchange);
						}
					});
				}
			}
			catch (Exception e) {
				log.error("JWT 토큰이 유효하지 않음"); ;
				return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("{\"error\": \"유효하지 않은 액세스 토큰.\"}".getBytes())));
			}
		}
		return chain.filter(exchange);
	}

	private Mono<Void> mutateExchange(ServerWebExchange exchange, WebFilterChain chain, String accessToken) {
		String email = jwtUtil.getEmailFromToken(accessToken); // JWT 토큰에서 Email추출
		String userId = jwtUtil.getUserIdFromToken(accessToken).toString();
		//추후에 여기 부분 판매자랑 나누면 될 것 같다
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

		ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
			.header(HttpHeaders.AUTHORIZATION, accessToken)
			.header("email", email)
			.header("userId", userId)
			.build();

		// 인증 객체 생성
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
		SecurityContext context = new SecurityContextImpl(authentication);

		ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

		return chain.filter(mutatedExchange)
			.contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)))
			.doOnEach(signal -> {
				if (signal.isOnComplete() || signal.isOnError()) {
					SecurityContextHolder.clearContext();
				}
			});
	}

	private Mono<String> requestNewAccessToken(ServerWebExchange exchange) {
		String email = exchange.getRequest().getHeaders().getFirst("email");

		return WebClient.builder().build().post()
			.uri("http://localhost:8081/user/api/refresh")
			.header("email", email)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> Mono.empty())
			.bodyToMono(String.class)
			.doOnSuccess(newToken -> {
				if (newToken != null) {
					log.info("새 토큰 발급 성공: {}", email);
				} else {
					log.warn("새 토큰 발급 실패: {}", email);
				}
			})
			.doOnError(e -> log.error("Token renewal request failed: {}", e.getMessage()));
	}

	private Mono<Void> handleTokenError(ServerWebExchange exchange) {
		log.error("JWT 토큰이 유효하지 않습니다.");
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("{\"error\": \"유효하지 않은 액세스 토큰.\"}".getBytes())));
	}
}
