package com.antonstrokov.jaide.plugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JaideBackendClient {

	private static final String EXPLAIN_URL = "http://localhost:8080/ai/explain";

	private final HttpClient httpClient = HttpClient.newHttpClient();

	public String explain(String selectedCode) throws IOException, InterruptedException {
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

		return extractSummary(response.body());
	}

	private String extractSummary(String responseBody) {
		String marker = "\"summary\":\"";
		int startIndex = responseBody.indexOf(marker);

		if (startIndex == -1) {
			return "Summary not found";
		}

		startIndex += marker.length();

		int endIndex = responseBody.indexOf("\"", startIndex);

		if (endIndex == -1) {
			return "Summary parsing failed";
		}

		return responseBody.substring(startIndex, endIndex);
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