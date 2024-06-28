package com.billing.util;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JwtTokenProvider {
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
	private static final String BEARER_PREFIX = "Bearer ";

	private final long accessTokenExpiredTime = 30 * 60 * 1000L;
	private final long refreshTokenExpiredTime = 7 * 24 * 60 * 60 * 1000L;

	private final byte[] keyBytes;

	public JwtTokenProvider(@Value("${spring.security.user-service.jwt.secret}") String secretKey) {
		try {
			this.keyBytes = Base64.getDecoder().decode(secretKey);
		} catch (IllegalArgumentException e) {
			throw e;
		}
	}

	public String createAccessToken(String email) {
		return createToken(email, accessTokenExpiredTime, TokenType.ACCESS);
	}

	public String createRefreshToken(String email) {
		return createToken(email, refreshTokenExpiredTime, TokenType.REFRESH);
	}

	private String createToken(String email, long expire, TokenType tokenType) {
		Claims claims = Jwts.claims();
		claims.put("email", email);
		claims.put("tokenType", tokenType.name());

		Date now = new Date();
		Date expireTime = new Date(now.getTime() + expire);


		String token = Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(expireTime)
			.signWith(getSigningKey(keyBytes))
			.compact();
		return BEARER_PREFIX + token;
	}

	public boolean isExpired(String token) {
		try {
			Date expiredDate = extractClaims(token).getExpiration();
			boolean isExpired = expiredDate.before(new Date());
			return isExpired;
		} catch (Exception e) {
			return true;
		}
	}

	public String getEmail(Claims claims) {
		String email = claims.get("email", String.class);
		return email;
	}

	public String getTokenType(Claims claims) {
		String tokenType = claims.get("tokenType", String.class);
		return tokenType;
	}

	public Claims extractClaims(String token) {
		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey(keyBytes))
				.build().parseClaimsJws(parseBearerToken(token)).getBody();
			return claims;
		} catch (Exception e) {
			return null;
		}
	}

	public Claims extractClaimsFromRefreshToken(String token) {
		return extractClaims(token);
	}

	private Key getSigningKey(byte[] keyBytes) {
		try {
			Key key = Keys.hmacShaKeyFor(keyBytes);
			return key;
		} catch (Exception e) {
			throw e;
		}
	}

	private String parseBearerToken(final String inputString) {
		if (inputString != null && inputString.startsWith(BEARER_PREFIX)) {
			String token = inputString.substring(BEARER_PREFIX.length());
			return token;
		}
		return null;
	}
}
