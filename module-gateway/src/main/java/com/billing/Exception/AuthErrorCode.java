package com.billing.Exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum AuthErrorCode {
	INVALID_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "인증 헤더가 Authorization이 아닙니다"),
	INVALID_BEARER_TOKEN(HttpStatus.BAD_REQUEST, "Bearer 토큰이 아닙니다"),
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다"),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 틀리셨어요"),
	INVALID_AUTHENTICATION_NUM(HttpStatus.BAD_REQUEST, "인증번호를 틀리셨어요"),
	INVALID_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "리프레시 토큰이 동일하지 않습니다"),
	NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "해당 리프레시 토큰의 클레임과 일치하는 유저가 없습니다"),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다");

	private final HttpStatus status;
	private final String message;

	AuthErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
