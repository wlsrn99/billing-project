package com.billing.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component

public class JwtUtil {
	// Header KEY 값
	public static final String AUTHORIZATION_HEADER = "Authorization";
	// Token 식별자
	public static final String BEAR = "Bearer ";

	// JWT secret key
	@Value("${spring.security.user-service.jwt.secret}")
	private String secretKey;

	// header 에서 JWT 가져오기
	public String getJwtFromHeader(ServerHttpRequest request) {
		String bearerToken = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
		if (bearerToken != null && bearerToken.startsWith(BEAR)) {
			return bearerToken.substring(7).trim(); // "Bearer "를 제거하고 공백 제거
		}
		return null;
	}

	public Claims getClaimsFromToken(String accessToken) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
	}
}