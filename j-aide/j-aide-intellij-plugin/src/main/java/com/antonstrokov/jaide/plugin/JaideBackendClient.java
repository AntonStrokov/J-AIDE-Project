package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.dto.JaideExplainResponse;
import com.antonstrokov.jaide.plugin.dto.JaideExplanation;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JaideBackendClient {

	private static final String EXPLAIN_URL = "http://localhost:8080/ai/explain";

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final ObjectMapper objectMapper = new ObjectMapper();

	public JaideExplanation explain(String selectedCode) throws IOException, InterruptedException {
		String requestBody = """
                {
                  "code": "%s",
                  "mode": "SMART",
                  "language": "java"
                }
                """.formatted(escapeJson(selectedCode));

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(EXPLAIN_URL))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();

		HttpResponse<String> response = httpClient.send(
				request,
				HttpResponse.BodyHandlers.ofString()
		);

		return parseExplanation(response.body());
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

	private String escapeJson(String value) {
		return value
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}
}