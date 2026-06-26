package com.antonstrokov.jaide.plugin.client;

import java.net.URI;
import java.net.http.HttpRequest;
import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public class JaideBackendTransport {
	private static final Logger log = Logger.getInstance(JaideBackendTransport.class);

	private final HttpClient httpClient = HttpClient.newHttpClient();

	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";

	public HttpRequest buildJsonPostRequest(String url, String requestBody) {
		return HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();
	}

	public HttpRequest buildGetRequest(String url) {
		return HttpRequest.newBuilder()
				.uri(URI.create(url))
				.GET()
				.build();
	}

	public String send(HttpRequest request) throws IOException, InterruptedException {
		log.info("Sending HTTP request to " + request.uri());

		long startedAt = System.currentTimeMillis();

		HttpResponse<String> response = httpClient.send(
				request,
				HttpResponse.BodyHandlers.ofString()
		);

		long responseTimeMs = System.currentTimeMillis() - startedAt;
		int statusCode = response.statusCode();

		if (statusCode < 200 || statusCode >= 300) {
			log.warn("Backend returned error response, statusCode=" + statusCode
					+ ", responseTimeMs=" + responseTimeMs
					+ ", responseBodyLength=" + getLength(response.body()));

			throw new JaideBackendException(statusCode, response.body());
		}

		log.info("Backend returned successful response, statusCode=" + statusCode
				+ ", responseTimeMs=" + responseTimeMs
				+ ", responseBodyLength=" + getLength(response.body()));

		return response.body();
	}

	private int getLength(String value) {
		return value == null ? 0 : value.length();
	}
}