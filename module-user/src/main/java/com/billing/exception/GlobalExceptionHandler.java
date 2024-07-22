package com.billing.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(final GlobalException e) {
		log.error("Error occurs", e);
		ErrorResponse errorResponse = ErrorResponse.builder()
			.status(e.getHttpStatus())
			.message(e.getMessage())
			.timestamp(LocalDateTime.now())
			.build();
		return ResponseEntity.status(e.getHttpStatus()).body(errorResponse);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(final RuntimeException e) {
		log.error("Error occurs {}", e);
		ErrorResponse errorResponse = ErrorResponse.builder()
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.message(e.getMessage())
			.timestamp(LocalDateTime.now())
			.build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}
