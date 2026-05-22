package com.antonstrokov.jaide.plugin.client;

import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.dto.backend.JaideBackendExplainRequest;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainRequest;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainResponse;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplanation;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplainRequest;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplainResponse;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplanation;
import com.antonstrokov.jaide.plugin.dto.improve.JaideBackendImproveRequest;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImproveRequest;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImproveResponse;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.antonstrokov.jaide.plugin.factory.error.JaideBackendErrorExplainRequestFactory;
import com.antonstrokov.jaide.plugin.factory.explain.JaideBackendExplainRequestFactory;
import com.antonstrokov.jaide.plugin.factory.improve.JaideBackendImproveRequestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JaideBackendClient {
	private static final Logger log = Logger.getInstance(JaideBackendClient.class);

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final JaideBackendExplainRequestFactory backendRequestFactory = new JaideBackendExplainRequestFactory();
	private final JaideBackendImproveRequestFactory improveRequestFactory = new JaideBackendImproveRequestFactory();
	private final JaideBackendErrorExplainRequestFactory errorExplainRequestFactory =
			new JaideBackendErrorExplainRequestFactory();

	public JaideExplanation explain(JaideExplainRequest request) throws IOException, InterruptedException {
		String requestBody = buildExplainRequestBody(request);

		log.info("Sending explain request to backend, requestBodyLength=" + requestBody.length());

		HttpRequest httpRequest = buildExplainHttpRequest(requestBody);

		String responseBody = send(httpRequest);

		log.info("Explain response received, responseBodyLength=" + responseBody.length());

		return parseExplanation(responseBody);
	}

	public JaideImprovement improve(JaideImproveRequest request) throws IOException, InterruptedException {
		String requestBody = buildImproveRequestBody(request);

		log.info("Sending improve request to backend, requestBodyLength=" + requestBody.length());

		HttpRequest httpRequest = buildImproveHttpRequest(requestBody);

		String responseBody = send(httpRequest);

		log.info("Improve response received, responseBodyLength=" + responseBody.length());

		return parseImprovement(responseBody);
	}

	public JaideErrorExplanation explainError(JaideErrorExplainRequest request)
			throws IOException, InterruptedException {
		String requestBody = buildErrorExplainRequestBody(request);

		log.info("Sending explain error request to backend, requestBodyLength=" + requestBody.length());

		HttpRequest httpRequest = buildErrorExplainHttpRequest(requestBody);

		String responseBody = send(httpRequest);

		log.info("Explain error response received, responseBodyLength=" + responseBody.length());

		return parseErrorExplanation(responseBody);
	}

	private String buildExplainRequestBody(JaideExplainRequest request) throws IOException {
		JaideBackendExplainRequest backendRequest = backendRequestFactory.create(request);

		return objectMapper.writeValueAsString(backendRequest);
	}

	private String buildImproveRequestBody(JaideImproveRequest request) throws IOException {
		JaideBackendImproveRequest backendRequest = improveRequestFactory.create(request);

		return objectMapper.writeValueAsString(backendRequest);
	}

	private String buildErrorExplainRequestBody(JaideErrorExplainRequest request) throws IOException {
		JaideErrorExplainRequest backendRequest = errorExplainRequestFactory.create(request);

		return objectMapper.writeValueAsString(backendRequest);
	}

	private HttpRequest buildExplainHttpRequest(String requestBody) {
		return buildJsonPostRequest(JaideConstants.EXPLAIN_URL, requestBody);
	}

	private HttpRequest buildImproveHttpRequest(String requestBody) {
		return buildJsonPostRequest(JaideConstants.IMPROVE_URL, requestBody);
	}

	private HttpRequest buildErrorExplainHttpRequest(String requestBody) {
		return buildJsonPostRequest(JaideConstants.EXPLAIN_ERROR_URL, requestBody);
	}

	private HttpRequest buildJsonPostRequest(String url, String requestBody) {
		return HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();
	}

	private String send(HttpRequest request) throws IOException, InterruptedException {
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

	private JaideErrorExplanation parseErrorExplanation(String responseBody) throws IOException {
		JaideErrorExplainResponse errorExplainResponse = objectMapper.readValue(
				responseBody,
				JaideErrorExplainResponse.class
		);

		if (errorExplainResponse.errorExplanation() == null) {
			return new JaideErrorExplanation(
					"Error explanation not found",
					null,
					null,
					null,
					null,
					null
			);
		}

		return errorExplainResponse.errorExplanation();
	}

	private int getLength(String value) {
		return value == null ? 0 : value.length();
	}
}