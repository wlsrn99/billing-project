package com.billing.filter;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.billing.Exception.AuthErrorCode;
import com.billing.Exception.AuthException;
import com.billing.util.JwtTokenProvider;
import com.billing.util.UserDetailServiceImpl;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationFilter implements WebFilter {
	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailServiceImpl userDetailsService;

	public JwtAuthenticationFilter(UserDetailServiceImpl userDetailsService, JwtTokenProvider jwtTokenProvider) {
		this.userDetailsService = userDetailsService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		// 로그인과 관련된 경로는 필터링하지 않음
		String path = exchange.getRequest().getURI().getPath();
		if (path.startsWith("/users/")) {
			return chain.filter(exchange);
		}
		return validateToken(exchange)
			.flatMap(auth -> {
				// 인증 정보를 SecurityContext에 적용
				return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
			})
			.onErrorResume(e -> {
				log.error("Authentication failed: {}", e.getMessage());
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			});
	}

	private Mono<Authentication> validateToken (ServerWebExchange exchange){
		return parseHeaders(exchange)
			.flatMap(token -> {
				if (!jwtTokenProvider.isExpired(token)) {
					Claims claims = jwtTokenProvider.extractClaims(token);
					String email = jwtTokenProvider.getEmail(claims);
					return userDetailsService.findByUsername(email)
						.map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
				} else {
					return Mono.error(new AuthException(AuthErrorCode.TOKEN_EXPIRED));
				}
			});
	}

	private Mono<String> parseHeaders(ServerWebExchange exchange) {
		String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
		if (authHeader == null) {
			return Mono.error(()-> new AuthException(AuthErrorCode.INVALID_AUTHORIZATION_HEADER));
		}
		if (!authHeader.startsWith("Bearer ")) {
			return Mono.error(()-> new AuthException(AuthErrorCode.INVALID_BEARER_TOKEN));
		}
		return Mono.just(authHeader.substring(7));
	}

}
