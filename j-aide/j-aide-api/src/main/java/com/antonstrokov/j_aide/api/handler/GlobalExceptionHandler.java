package com.antonstrokov.j_aide.api.handler;

import com.antonstrokov.j_aide.api.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
		log.warn("Invalid request: {}", ex.getMessage());
		return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {

		log.error("Unhandled exception occurred", ex);

		return ResponseEntity
				.internalServerError()
				.body(new ErrorResponse(ex.getMessage()));
	}
}