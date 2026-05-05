package com.antonstrokov.jaide.plugin.error;

public class JaideErrorMessageBuilder {

	public String build(Exception ex) {
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

	private boolean containsConnectionRefused(String value) {
		return value != null && value.contains("Connection refused");
	}
}