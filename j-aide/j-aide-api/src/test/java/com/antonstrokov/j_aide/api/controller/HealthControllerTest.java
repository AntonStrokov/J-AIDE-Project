package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.api.dto.backend.BackendHealthStatus;
import com.antonstrokov.j_aide.core.config.AiProperties;
import com.antonstrokov.j_aide.core.config.AppProperties;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthDiagnosticCode;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthResult;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthStatus;
import com.antonstrokov.j_aide.core.service.AiProviderHealthService;
import com.antonstrokov.j_aide.core.service.HealthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class HealthControllerTest {

	private static final String MODEL = "jaide-test-model:custom";
	private static final String MESSAGE = "Configured model is unavailable.";

	private final ObjectMapper objectMapper = new ObjectMapper();

	private AiProviderHealthService aiProviderHealthService;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		aiProviderHealthService = mock(AiProviderHealthService.class);

		AiProviderHealthResult result = new AiProviderHealthResult(
				AiProviderHealthStatus.READY,
				AiProviderHealthStatus.READY,
				AiProviderHealthStatus.FAILED,
				"test-version",
				12L,
				MESSAGE,
				AiProviderHealthDiagnosticCode.MODEL_NOT_FOUND,
				MODEL
		);

		when(aiProviderHealthService.getHealthInfo()).thenReturn(result);
		when(aiProviderHealthService.getSetupHealthInfo()).thenReturn(result);

		AiProperties properties = new AiProperties(
				new AiProperties.Ollama(
						"http://127.0.0.1:11434",
						MODEL,
						0.1,
						60
				),
				new AiProperties.Limits(2000, 15000, 2000)
		);

		HealthController controller = new HealthController(
				mock(HealthService.class),
				mock(AppProperties.class),
				properties,
				aiProviderHealthService
		);

		mockMvc = standaloneSetup(controller).build();
	}

	@Test
	void shouldKeepLegacyAiHealthResponseByDefault() throws Exception {
		String json = mockMvc.perform(get("/ai/health"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertLegacyHealthResponse(json);

		verify(aiProviderHealthService).getSetupHealthInfo();
		verify(aiProviderHealthService, never()).getHealthInfo();
	}

	@Test
	void shouldIncludeDiagnosticsWhenExplicitlyRequested() throws Exception {
		String json = mockMvc.perform(
						get("/ai/health").param("diagnostics", "true")
				)
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode response = objectMapper.readTree(json);

		assertEquals(8, response.size());
		assertEquals("READY", response.path("backendStatus").asText());
		assertEquals("READY", response.path("providerStatus").asText());
		assertEquals("FAILED", response.path("modelStatus").asText());
		assertEquals(
				"MODEL_NOT_FOUND",
				response.path("diagnosticCode").asText()
		);
		assertEquals(MODEL, response.path("configuredModel").asText());

		verify(aiProviderHealthService).getSetupHealthInfo();
	}

	@Test
	void shouldKeepLegacyHealthFieldsInBackendInfo() throws Exception {
		String json = mockMvc.perform(get("/backend-info"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode health = objectMapper.readTree(json).path("health");

		assertLegacyHealthResponse(health.toString());

		verify(aiProviderHealthService).getHealthInfo();
		verify(aiProviderHealthService, never()).getSetupHealthInfo();
	}

	private void assertLegacyHealthResponse(String json)
			throws JsonProcessingException {
		assertEquals(6, objectMapper.readTree(json).size());

		LegacyHealthResponse expected = new LegacyHealthResponse(
				BackendHealthStatus.READY,
				BackendHealthStatus.READY,
				BackendHealthStatus.FAILED,
				"test-version",
				12L,
				MESSAGE
		);

		assertEquals(
				expected,
				objectMapper.readValue(json, LegacyHealthResponse.class)
		);
	}

	private record LegacyHealthResponse(
			BackendHealthStatus backendStatus,
			BackendHealthStatus providerStatus,
			BackendHealthStatus modelStatus,
			String providerVersion,
			Long responseTimeMs,
			String message
	) {
	}
}
