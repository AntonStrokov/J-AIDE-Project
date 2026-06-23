package com.antonstrokov.jaide.plugin.client;

import java.net.URI;
import java.net.http.HttpRequest;

public class JaideBackendTransport {
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";

	public HttpRequest buildJsonPostRequest(String url, String requestBody) {
		return HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();
	}
}