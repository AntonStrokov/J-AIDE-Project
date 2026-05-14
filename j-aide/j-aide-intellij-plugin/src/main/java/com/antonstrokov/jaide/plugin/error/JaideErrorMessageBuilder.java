package com.antonstrokov.jaide.plugin.error;

import com.antonstrokov.jaide.plugin.client.JaideBackendException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JaideErrorMessageBuilder {
	private final ObjectMapper objectMapper = new ObjectMapper();

	public String build(Exception ex) {
		if (ex instanceof JaideBackendException backendException) {
			return buildBackendErrorMessage(backendException);
		}

		String message = ex.getMessage();
		Throwable cause = ex.getCause();
		String causeMessage = cause == null ? null : cause.getMessage();

		if (containsConnectionRefused(message) || containsConnectionRefused(causeMessage)) {
			return "J-Aide backend is not available. Please start the backend on http://localhost:8080.";
		}

		if (message == null || message.isBlank()) {
			return "J-Aide backend error. Please check that the backend is running.";
		}

		return "J-Aide backend error: " + message;
	}

	private String buildBackendErrorMessage(JaideBackendException ex) {
		String backendError = extractBackendError(ex.responseBody());

		if (backendError != null && !backendError.isBlank()) {
			return "J-Aide request failed: " + backendError;
		}

		return "J-Aide backend error. HTTP status: " + ex.statusCode();
	}

	private String extractBackendError(String responseBody) {
		if (responseBody == null || responseBody.isBlank()) {
			return null;
		}

		try {
			JaideBackendErrorResponse errorResponse = objectMapper.readValue(
					responseBody,
					JaideBackendErrorResponse.class
			);

			return errorResponse.error();

		} catch (IOException ex) {
			return null;
		}
	}

	private boolean containsConnectionRefused(String value) {
		return value != null && value.contains("Connection refused");
	}
}