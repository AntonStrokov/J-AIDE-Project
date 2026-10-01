package com.antonstrokov.jaide.plugin.dto.health;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.antonstrokov.jaide.plugin.dto.health.JaideHealthStatus.FAILED;
import static com.antonstrokov.jaide.plugin.dto.health.JaideHealthStatus.READY;
import static com.antonstrokov.jaide.plugin.dto.health.JaideHealthStatus.UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JaideHealthResponseJsonTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void shouldReadDiagnosticCodeAndConfiguredModel()
			throws JsonProcessingException {
		String json = """
                {
                  "backendStatus": "READY",
                  "providerStatus": "READY",
                  "modelStatus": "FAILED",
                  "providerVersion": "test-version",
                  "responseTimeMs": 12,
                  "message": "Configured model is unavailable.",
                  "diagnosticCode": "MODEL_NOT_FOUND",
                  "configuredModel": "qwen2.5-coder:7b"
                }
                """;

		JaideHealthResponse response =
				objectMapper.readValue(json, JaideHealthResponse.class);

		assertEquals(READY, response.backendStatus());
		assertEquals(READY, response.providerStatus());
		assertEquals(FAILED, response.modelStatus());
		assertEquals("MODEL_NOT_FOUND", response.diagnosticCode());
		assertEquals("qwen2.5-coder:7b", response.configuredModel());
	}

	@Test
	void shouldReadOlderResponseWithoutDiagnosticFields()
			throws JsonProcessingException {
		String json = """
                {
                  "backendStatus": "READY",
                  "providerStatus": "READY",
                  "modelStatus": "READY",
                  "providerVersion": "test-version",
                  "responseTimeMs": 12,
                  "message": "AI provider and configured model are ready."
                }
                """;

		JaideHealthResponse response =
				objectMapper.readValue(json, JaideHealthResponse.class);

		assertEquals(READY, response.backendStatus());
		assertEquals(READY, response.providerStatus());
		assertEquals(READY, response.modelStatus());
		assertNull(response.diagnosticCode());
		assertNull(response.configuredModel());
	}

	@Test
	void shouldAcceptUnknownDiagnosticCodeAndAdditionalFields()
			throws JsonProcessingException {
		String json = """
                {
                  "backendStatus": "READY",
                  "providerStatus": "READY",
                  "modelStatus": "UNKNOWN",
                  "providerVersion": "test-version",
                  "responseTimeMs": 12,
                  "message": "A future diagnostic result.",
                  "diagnosticCode": "FUTURE_DIAGNOSTIC_CODE",
                  "configuredModel": "qwen2.5-coder:7b",
                  "futureDetails": {
                    "additionalCheck": true
                  }
                }
                """;

		JaideHealthResponse response =
				objectMapper.readValue(json, JaideHealthResponse.class);

		assertEquals(UNKNOWN, response.modelStatus());
		assertEquals("FUTURE_DIAGNOSTIC_CODE", response.diagnosticCode());
		assertEquals("qwen2.5-coder:7b", response.configuredModel());
	}
}
