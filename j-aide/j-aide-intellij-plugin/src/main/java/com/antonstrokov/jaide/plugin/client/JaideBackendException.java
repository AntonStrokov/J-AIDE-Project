package com.antonstrokov.jaide.plugin.client;

public class JaideBackendException extends RuntimeException {
	private final int statusCode;
	private final String responseBody;

	public JaideBackendException(int statusCode, String responseBody) {
		super(buildMessage(statusCode, responseBody));
		this.statusCode = statusCode;
		this.responseBody = responseBody;
	}

	public int statusCode() {
		return statusCode;
	}

	public String responseBody() {
		return responseBody;
	}

	private static String buildMessage(int statusCode, String responseBody) {
		if (responseBody == null || responseBody.isBlank()) {
			return "Backend request failed with HTTP status " + statusCode;
		}

		return "Backend request failed with HTTP status " + statusCode + ": " + responseBody;
	}
}