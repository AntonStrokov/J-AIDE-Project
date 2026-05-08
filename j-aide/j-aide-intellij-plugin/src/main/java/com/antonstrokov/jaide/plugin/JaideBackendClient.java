package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.dto.backend.JaideBackendExplainRequest;
import com.antonstrokov.jaide.plugin.dto.JaideExplainRequest;
import com.antonstrokov.jaide.plugin.dto.JaideExplainResponse;
import com.antonstrokov.jaide.plugin.dto.JaideExplanation;
import com.antonstrokov.jaide.plugin.factory.JaideBackendExplainRequestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JaideBackendClient {
	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final JaideBackendExplainRequestFactory backendRequestFactory = new JaideBackendExplainRequestFactory();

	public JaideExplanation explain(JaideExplainRequest request) throws IOException, InterruptedException {
		String requestBody = buildExplainRequestBody(request);

		HttpRequest httpRequest = buildExplainHttpRequest(requestBody);

		String responseBody = send(httpRequest);

		return parseExplanation(responseBody);
	}

	private String buildExplainRequestBody(JaideExplainRequest request) throws IOException {
		JaideBackendExplainRequest backendRequest = backendRequestFactory.create(request);

		return objectMapper.writeValueAsString(backendRequest);
	}

	private HttpRequest buildExplainHttpRequest(String requestBody) {
		return HttpRequest.newBuilder()
				.uri(URI.create(JaideConstants.EXPLAIN_URL))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();
	}

	private String send(HttpRequest request) throws IOException, InterruptedException {
		HttpResponse<String> response = httpClient.send(
				request,
				HttpResponse.BodyHandlers.ofString()
		);

		return response.body();
	}

	private JaideExplanation parseExplanation(String responseBody) throws IOException {
		JaideExplainResponse explainResponse = objectMapper.readValue(
				responseBody,
				JaideExplainResponse.class
		);

		if (explainResponse.explanation() == null) {
			return new JaideExplanation(
					"Summary not found",
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null
			);
		}

		return explainResponse.explanation();
	}
}