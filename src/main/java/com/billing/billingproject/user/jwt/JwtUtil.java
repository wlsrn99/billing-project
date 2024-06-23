package com.billing.billingproject.user.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
	// Header KEY 값
	public static final String AUTHORIZATION_HEADER = "Authorization";
	// 리프레시 헤더 값
	public static final String REFRESH_HEADER = "RefreshToken";
	// Token 식별자
	public static final String BEAR = "Bearer ";
	// 토큰 만료시간 (30분)
	private static final long TOKEN_TIME = 30 * 60 * 1000L;
	// 리프레시 토큰 만료시간 (7일)
	private static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L;
	//로그아웃 토큰 블랙리스트
	private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

	// JWT secret key
	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] accessKeyBytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(accessKeyBytes);
	}

	// 토큰 생성 공통 로직
	private String createToken(String subject, long expirationTime) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationTime);

		JwtBuilder builder = Jwts.builder()
			.setSubject(subject)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(key, signatureAlgorithm);

		return BEAR + builder.compact();
	}

	// 액세스 토큰 생성
	public String createAccessToken(String email) {
		return createToken(email, TOKEN_TIME);
	}

	// 리프레시 토큰 생성
	public String createRefreshToken(String email) {
		return createToken(email, REFRESH_TOKEN_TIME);
	}

	// header에서 JWT 가져오기
	public String getJwtFromHeader(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (bearerToken != null && bearerToken.startsWith(BEAR)) {
			return bearerToken.substring(BEAR.length());
		}
		return null;
	}

	// header에서 JWT 리프레시 토큰 가져오기
	public String getJwtRefreshTokenFromHeader(HttpServletRequest request) {
		String bearerToken = request.getHeader(REFRESH_HEADER);
		if (bearerToken != null && bearerToken.startsWith(BEAR)) {
			return bearerToken.substring(BEAR.length());
		}
		return null;
	}

	// 토큰 검증
	public boolean validateToken(String token) {
		return validateTokenInternal(token);
	}

	// 리프레시 토큰 검증
	public boolean validateRefreshToken(String token) {
		return validateTokenInternal(token);
	}

	// 토큰 검증 공통 로직
	private boolean validateTokenInternal(String token) {
		if (isTokenBlacklisted(token)) {
			throw new IllegalArgumentException("이미 로그아웃된 토큰입니다.");
		}

		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			log.error("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.", e);
			throw e;
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
			throw e;
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
			throw e;
		} catch (IllegalArgumentException e) {
			log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.", e);
			throw e;
		} catch (Exception e){
			log.error("잘못되었습니다.", e);
			throw e;
		}
	}

	// 토큰에서 username 가져오기
	public String getUsernameFromToken(String token) {
		return getUsernameFromClaims(token);
	}

	// 리프레시 토큰에서 username 가져오기
	public String getUsernameFromRefreshToken(String token) {
		return getUsernameFromClaims(token);
	}

	// 공통 로직 분리
	private String getUsernameFromClaims(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	// 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
	public String refreshAccessToken(String refreshToken) {
		if (validateRefreshToken(refreshToken)) {
			String email = getUsernameFromRefreshToken(refreshToken);
			// 여기에서 필요한 경우 사용자 역할 정보를 가져올 수 있다.
			return createAccessToken(email); // 사용자 역할이 필요하면 두 번째 인자에 역할을 전달
		}
		return null;
	}

	//토큰 블랙리스트 추가
	public void invalidateToken(String token) {
		tokenBlacklist.add(token);
	}

	//토큰 블랙리스트 검사
	private boolean isTokenBlacklisted(String token) {
		return tokenBlacklist.contains(token);
	}
}
