package com.billing;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import com.billing.user.jwt.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;

@SpringBootTest(classes = UserModuleApplication.class)
@ActiveProfiles("test")
public class JwtUtilTest {

	@Autowired
	private JwtUtil jwtUtil;

	private String email;
	private List<String> roles;
	private Long userId;

	@BeforeEach
	void setUp() {
		email = "test@gmail.com";
		roles = Collections.singletonList("ROLE_USER");
		userId = 1L;
	}

	@Test
	@DisplayName("액세스 토큰 생성")
	void createAccessTokenTest() {
		// when
		String token = jwtUtil.createAccessToken(email, roles, userId);

		// then
		assertThat(token).isNotNull();
		assertThat(token).startsWith(JwtUtil.BEAR);
	}

	@Test
	@DisplayName("리프레시 토큰 생성")
	void createRefreshTokenTest() {
		// when
		String token = jwtUtil.createRefreshToken(email, roles, userId);

		// then
		assertThat(token).isNotNull();
		assertThat(token).doesNotStartWith(JwtUtil.BEAR);
	}

	@Test
	@DisplayName("토큰 검증")
	void validateTokenTest() {
		// given
		String token = jwtUtil.createAccessToken(email, roles, userId).substring(7);

		// when
		boolean isValid = jwtUtil.validateToken(token);

		// then
		assertThat(isValid).isTrue();
	}

	@Test
	@DisplayName("블랙리스트된 토큰 검증")
	void validateBlacklistedTokenTest() {
		// given
		String token = jwtUtil.createAccessToken(email, roles, userId).substring(7);

		//when
		jwtUtil.invalidateToken(token);

		//then
		assertThatThrownBy(() -> jwtUtil.validateToken(token))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("이미 로그아웃된 토큰입니다.");
	}

	@Test
	@DisplayName("토큰에서 이메일 추출")
	void getEmailFromTokenTest() {
		// given
		String token = jwtUtil.createAccessToken(email, roles, userId).substring(7);

		// when
		String extractedEmail = jwtUtil.getEmailFromToken(token);

		// then
		assertThat(extractedEmail).isEqualTo(email);
	}

	@Test
	@DisplayName("리프레시 토큰에서 역할 추출")
	void getRolesFromRefreshTokenTest() {
		// given
		String token = jwtUtil.createRefreshToken(email, roles, userId);

		// when
		List<String> extractedRoles = jwtUtil.getRolesFromRefreshToken(token);

		// then
		assertThat(extractedRoles).isEqualTo(roles);
	}

	@Test
	@DisplayName("토큰에서 사용자 ID 추출")
	void getUserIdFromTokenTest() {
		// given
		String token = jwtUtil.createAccessToken(email, roles, userId).substring(7);

		// when
		Long extractedUserId = jwtUtil.getUserIdFromToken(token);

		// then
		assertThat(extractedUserId).isEqualTo(userId);
	}

	@Test
	@DisplayName("리프레시 토큰으로 새 액세스 토큰 발급 성공")
	void refreshAccessTokenSuccessTest() {
		// given
		String refreshToken = jwtUtil.createRefreshToken(email, roles, userId);

		// when
		String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);

		// then
		assertThat(newAccessToken).isNotNull();
		assertThat(newAccessToken).startsWith(JwtUtil.BEAR);

		// 새 액세스 토큰의 클레임 검증
		String tokenWithoutPrefix = newAccessToken.substring(7);
		assertThat(jwtUtil.getEmailFromToken(tokenWithoutPrefix)).isEqualTo(email);
		assertThat(jwtUtil.getUserIdFromToken(tokenWithoutPrefix)).isEqualTo(userId);
	}

	@Test
	@DisplayName("유효하지 않은 리프레시 토큰으로 새 액세스 토큰 발급 실패")
	void refreshAccessTokenFailureTest() {
		// given
		String invalidRefreshToken = "invalidRefreshToken";

		// when & then
		assertThatThrownBy(() -> jwtUtil.refreshAccessToken(invalidRefreshToken))
			.isInstanceOf(MalformedJwtException.class);
	}


	@Test
	@DisplayName("유효한 Bearer 토큰이 헤더에 있을 때 JWT 추출")
	void getJwtFromHeaderSuccessTest() {
		// Given
		MockHttpServletRequest request = new MockHttpServletRequest();
		String token = "validToken";
		request.addHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEAR + "  " + token + "  ");

		// When
		String extractedToken = jwtUtil.getJwtFromHeader(request);

		// Then
		assertThat(extractedToken).isEqualTo(token);
	}

	@Test
	@DisplayName("Bearer 접두사 없이 토큰이 헤더에 있을 때 null 반환")
	void getJwtFromHeader_WithoutBearerPrefix() {
		// Given
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(JwtUtil.AUTHORIZATION_HEADER, "validToken");

		// When
		String extractedToken = jwtUtil.getJwtFromHeader(request);

		// Then
		assertThat(extractedToken).isNull();
	}

	@Test
	@DisplayName("Authorization 헤더가 없을 때 null 반환")
	void getJwtFromHeader_WithoutAuthorizationHeader() {
		// Given
		MockHttpServletRequest request = new MockHttpServletRequest();

		// When
		String extractedToken = jwtUtil.getJwtFromHeader(request);

		// Then
		assertThat(extractedToken).isNull();
	}
}