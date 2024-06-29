package com.billing.util;

import java.security.Key;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtUtil {
	// Header KEY 값
	public static final String AUTHORIZATION_HEADER = "Authorization";
	// Token 식별자
	public static final String BEAR = "Bearer ";

	// JWT secret key
	@Value("${spring.security.user-service.jwt.secret}")
	private String secretKey;
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] accessKeyBytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(accessKeyBytes);
	}

	// JWT 토큰에서 클레임을 추출
	public Mono<Claims> getClaimsFromToken(String token) {
		String originalToken = removeBearerPrefix(token);

		return Mono.fromCallable(() -> {
			try {
				log.info("JWT 토큰에서 클레임 추출");

				return Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(originalToken)
					.getBody();
			} catch (ExpiredJwtException e) {
				// 만료된 토큰의 경우에도 클레임을 반환
				log.warn("만료된 JWT 토큰: {}", e.getMessage());
				return e.getClaims();
			} catch (Exception e) {
				log.error("유효하지 않은 JWT 토큰: {}", e.getMessage());
				log.error("유효하지 않은 JWT 토큰: {}", originalToken);
				throw new RuntimeException("Invalid JWT token", e);
			}
		}).onErrorResume(e -> {
			if (e instanceof RuntimeException) {
				return Mono.error(e); // RuntimeException 이면 그대로 반환
			} else {
				return Mono.error(new RuntimeException("Unexpected error occurred", e));
			}
		});
	}

	// HTTP 요청 헤더에서 JWT 토큰을 추출하는 메서드
	public String resolveToken(ServerHttpRequest request) {
		String bearerToken = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEAR)) {
			log.info("HTTP 요청 헤더에서 JWT 토큰 추출 성공! {}", bearerToken);
			return bearerToken.substring(7);
		}
		return null;
	}

	// JWT 토큰을 기반으로 인증 정보를 설정하는 메서드
	public void setAuthentication(String token) {
		String originalToken = removeBearerPrefix(token);
		getClaimsFromToken(originalToken).subscribe(claims -> {
			String email = claims.getSubject();
			List<String> roles = claims.get("roles", List.class);
			log.info("JWT 토큰에서 사용자 정보를 추출하여 인증을 설정 사용자: {}, 권한: {}", email, roles);

			// 사용자 정보를 UserDetails 객체로 변환
			UserDetails userDetails = User.builder()
				.username(email)
				.password("") // 비밀번호는 필요하지 않음
				.authorities(
					roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
				.build();

			// 인증 객체 생성
			UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

			// SecurityContext 에 인증 객체 설정
			ReactiveSecurityContextHolder.getContext().flatMap(ctx -> {
				ctx.setAuthentication(authentication);
				return Mono.just(ctx);
			}).subscribe();
		});
	}

	public String getEmailFromToken(String token) {
		String originalToken = removeBearerPrefix(token);
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(originalToken).getBody();
		return claims.getSubject();
	}

	public boolean validateToken(String accessToken) {
		return validateTokenInternal(accessToken);
	}

	// 토큰 검증 공통 로직
	private boolean validateTokenInternal(String token) {
		try {
			String originalToken = removeBearerPrefix(token);
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(originalToken);
			return true;
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			log.error("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.", e);
			return false;
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
			return false;
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
			return false;
		} catch (IllegalArgumentException e) {
			log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.", e);
			return false;
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			return false;
		}
	}

	// Bearer 접두사를 제거하는 유틸리티 메서드
	public static String removeBearerPrefix(String token) {
		if (token != null && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		return token;
	}
}
