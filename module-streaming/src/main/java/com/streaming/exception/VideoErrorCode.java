package com.streaming.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum VideoErrorCode {
	VIDEO_NOT_FOUND(HttpStatus.NOT_FOUND, "영상을 찾을 수 없습니다");

	private final HttpStatus status;
	private final String message;

	VideoErrorCode(final HttpStatus status, final String message) {
		this.status = status;
		this.message = message;
	}
}
