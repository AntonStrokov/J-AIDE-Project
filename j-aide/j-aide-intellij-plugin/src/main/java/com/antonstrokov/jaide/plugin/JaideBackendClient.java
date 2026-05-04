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
	private static final String PLUGIN_VERSION = "0.1.0";

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final ObjectMapper objectMapper = new ObjectMapper();

	public JaideExplanation explain(
			String selectedCode,
			String fileName,
			int lineStart,
			int lineEnd,
			String projectName,
			String moduleName,
			String ideVersion
	) throws IOException, InterruptedException {
		String language = resolveLanguage(fileName);

		String requestBody = buildExplainRequestBody(
				selectedCode,
				fileName,
				lineStart,
				lineEnd,
				projectName,
				moduleName,
				ideVersion
		);

		HttpRequest request = buildExplainHttpRequest(requestBody);

		String responseBody = send(request);

		return parseExplanation(responseBody);
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
		if (value == null) {
			return "";
		}

		return value
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}

	private String resolveLanguage(String fileName) {
		String extension = resolveExtension(fileName);

		return switch (extension) {
			case "java" -> "java";
			case "kt" -> "kotlin";
			case "sql" -> "sql";
			case "xml" -> "xml";
			case "js" -> "javascript";
			default -> "plain_text";
		};
	}

	private String resolveExtension(String fileName) {
		if (fileName == null || fileName.isBlank()) {
			return "";
		}

		int dotIndex = fileName.lastIndexOf('.');

		if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
			return "";
		}

		return fileName.substring(dotIndex + 1).toLowerCase();
	}

	private String buildExplainRequestBody(
			String selectedCode,
			String fileName,
			int lineStart,
			int lineEnd,
			String projectName,
			String moduleName,
			String ideVersion
	) {
		String language = resolveLanguage(fileName);

		return """
            {
              "code": "%s",
              "mode": "SMART",
              "language": "%s",
              "fileName": "%s",
              "lineStart": %d,
              "lineEnd": %d,
              "projectName": "%s",
              "moduleName": "%s",
              "pluginVersion": "%s",
              "ideVersion": "%s"
            }
            """.formatted(
				escapeJson(selectedCode),
				escapeJson(language),
				escapeJson(fileName),
				lineStart,
				lineEnd,
				escapeJson(projectName),
				escapeJson(moduleName),
				escapeJson(PLUGIN_VERSION),
				escapeJson(ideVersion)
		);
	}

	private HttpRequest buildExplainHttpRequest(String requestBody) {
		return HttpRequest.newBuilder()
				.uri(URI.create(EXPLAIN_URL))
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
}