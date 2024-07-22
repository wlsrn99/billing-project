package com.billing.exception;


import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final String message;

	public GlobalException(final HttpStatus httpStatus, final String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
