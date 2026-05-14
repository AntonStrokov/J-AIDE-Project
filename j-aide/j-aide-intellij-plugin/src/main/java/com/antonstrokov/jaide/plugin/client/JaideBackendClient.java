package com.antonstrokov.jaide.plugin.client;

import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.dto.backend.JaideBackendExplainRequest;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplainRequest;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplainResponse;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplanation;
import com.antonstrokov.jaide.plugin.factory.explain.JaideBackendExplainRequestFactory;
import com.antonstrokov.jaide.plugin.dto.improve.JaideBackendImproveRequest;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImproveRequest;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImproveResponse;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.antonstrokov.jaide.plugin.factory.improve.JaideBackendImproveRequestFactory;
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
	private final JaideBackendImproveRequestFactory improveRequestFactory = new JaideBackendImproveRequestFactory();

	public JaideExplanation explain(JaideExplainRequest request) throws IOException, InterruptedException {
		String requestBody = buildExplainRequestBody(request);

		HttpRequest httpRequest = buildExplainHttpRequest(requestBody);

		String responseBody = send(httpRequest);

		return parseExplanation(responseBody);
	}

	public JaideImprovement improve(JaideImproveRequest request) throws IOException, InterruptedException {
		String requestBody = buildImproveRequestBody(request);

		HttpRequest httpRequest = buildImproveHttpRequest(requestBody);

		String responseBody = send(httpRequest);

		return parseImprovement(responseBody);
	}

	private String buildExplainRequestBody(JaideExplainRequest request) throws IOException {
		JaideBackendExplainRequest backendRequest = backendRequestFactory.create(request);

		return objectMapper.writeValueAsString(backendRequest);
	}

	private String buildImproveRequestBody(JaideImproveRequest request) throws IOException {
		JaideBackendImproveRequest backendRequest = improveRequestFactory.create(request);

		return objectMapper.writeValueAsString(backendRequest);
	}

	private HttpRequest buildExplainHttpRequest(String requestBody) {
		return HttpRequest.newBuilder()
				.uri(URI.create(JaideConstants.EXPLAIN_URL))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();
	}

	private HttpRequest buildImproveHttpRequest(String requestBody) {
		return HttpRequest.newBuilder()
				.uri(URI.create(JaideConstants.IMPROVE_URL))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();
	}

	private String send(HttpRequest request) throws IOException, InterruptedException {
		HttpResponse<String> response = httpClient.send(
				request,
				HttpResponse.BodyHandlers.ofString()
		);

		int statusCode = response.statusCode();

		if (statusCode < 200 || statusCode >= 300) {
			throw new JaideBackendException(statusCode, response.body());
		}

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

	private JaideImprovement parseImprovement(String responseBody) throws IOException {
		JaideImproveResponse improveResponse = objectMapper.readValue(
				responseBody,
				JaideImproveResponse.class
		);

		if (improveResponse.improvement() == null) {
			return new JaideImprovement(
					"Improvement not found",
					null,
					null,
					null,
					null
			);
		}

		return improveResponse.improvement();
	}
}