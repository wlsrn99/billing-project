package com.billing.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum UserErrorCode {
	UNAUTHORIZED_ACCESS_ERROR(HttpStatus.UNAUTHORIZED, "로그인되어 있는 유저가 아닙니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "가입된 회원을 찾을 수 없습니다."),
	EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 이메일이 존재합니다."),
	INVALID_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "유효한 비밀번호 형식이 아닙니다."),
	INVALID_EMAIL_ERROR(HttpStatus.BAD_REQUEST, "유효한 이메일 형식이 아닙니다.");

	private final HttpStatus status;
	private final String message;

	UserErrorCode(final HttpStatus status, final String message) {
		this.status = status;
		this.message = message;
	}
}
