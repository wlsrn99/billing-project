package com.billing.Exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum UserErrorCode {
	UNAUTHORIZED_ACCESS_ERROR(HttpStatus.UNAUTHORIZED, "[ERROR] 권한이 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "[ERROR] 가입된 회원을 찾을 수 없습니다."),
	USER_DUPLICATED(HttpStatus.CONFLICT, "[ERROR] 이미 회원이 존재합니다."),
	INVALID_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "[ERROR] 비밀번호는 8자리 이상 20자리 이하이어야 합니다."),
	INVALID_EMAIL_ERROR(HttpStatus.BAD_REQUEST, "[ERROR] 유효한 이메일 형식이 아닙니다.");

	private final HttpStatus status;
	private final String message;

	UserErrorCode(final HttpStatus status, final String message) {
		this.status = status;
		this.message = message;
	}
}
