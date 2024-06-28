package com.billing.user.jwt;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.billing.user.dto.LoginRequestDTO;
import com.billing.user.dto.ResponseMessage;
import com.billing.user.entity.User;
import com.billing.user.repository.UserRepository;
import com.billing.user.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			LoginRequestDTO requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);

			return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(
					requestDto.getEmail(),
					requestDto.getPassword(),
					null
				)
			);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	//로그인 성공시
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
		String email = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getEmail();

		String accessToken = jwtUtil.createAccessToken(email);

		// 응답 헤더에 토큰 추가
		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
		log.info("User = {}, message = {}", email, "로그인에 성공했습니다.");
	}


	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
		response.setStatus(400);
		response.setContentType("application/json; charset=UTF-8");
		try {
			response.getWriter().write("{\"message\":\"회원을 찾을 수 없습니다.\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
