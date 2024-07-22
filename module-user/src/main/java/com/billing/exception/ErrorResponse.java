package com.billing.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

	private final HttpStatus status;
	private final String message;
	private final LocalDateTime timestamp;

	@Builder
	public ErrorResponse(final HttpStatus status, final String message, final LocalDateTime timestamp) {
		this.status = status;
		this.message = message;
		this.timestamp = timestamp;
	}
}
