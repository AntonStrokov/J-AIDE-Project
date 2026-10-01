package com.antonstrokov.j_aide.core.service;

import com.antonstrokov.j_aide.core.config.AiProperties;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthDiagnosticCode;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthResult;
import com.antonstrokov.j_aide.core.integration.ollama.OllamaClient;
import com.antonstrokov.j_aide.core.integration.ollama.dto.OllamaModelInfo;
import com.antonstrokov.j_aide.core.integration.ollama.dto.OllamaTagsResponse;
import com.antonstrokov.j_aide.core.integration.ollama.dto.OllamaVersionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.antonstrokov.j_aide.core.dto.health.AiProviderHealthStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AiProviderHealthServiceTest {

	private static final String CONFIGURED_MODEL = "qwen2.5-coder:7b";

	private OllamaClient ollamaClient;
	private AiProviderHealthService service;

	private static Stream<Arguments> invalidModelLists() {
		return Stream.of(
				Arguments.of(
						"Missing response",
						null
				),
				Arguments.of(
						"Missing model list",
						new OllamaTagsResponse(null)
				),
				Arguments.of(
						"Null model entry",
						new OllamaTagsResponse(Collections.singletonList(null))
				),
				Arguments.of(
						"Model without name or identifier",
						new OllamaTagsResponse(
								List.of(mock(OllamaModelInfo.class))
						)
				)
		);
	}

	@BeforeEach
	void setUp() {
		AiProperties properties = new AiProperties(
				new AiProperties.Ollama(
						"http://127.0.0.1:11434",
						CONFIGURED_MODEL,
						0.1,
						60
				),
				new AiProperties.Limits(2000, 15000, 2000)
		);

		ollamaClient = mock(OllamaClient.class);

		OllamaVersionResponse versionResponse =
				mock(OllamaVersionResponse.class);

		when(versionResponse.version()).thenReturn("test-version");
		when(ollamaClient.getVersion()).thenReturn(versionResponse);

		service = new AiProviderHealthService(ollamaClient, properties);
	}

	@Test
	void shouldReportMissingModelWithoutTrialGeneration() {
		when(ollamaClient.getModels())
				.thenReturn(new OllamaTagsResponse(List.of()));

		AiProviderHealthResult result = service.getSetupHealthInfo();

		assertEquals(READY, result.backendStatus());
		assertEquals(READY, result.providerStatus());
		assertEquals(FAILED, result.modelStatus());
		assertEquals(
				AiProviderHealthDiagnosticCode.MODEL_NOT_FOUND,
				result.diagnosticCode()
		);
		assertEquals(CONFIGURED_MODEL, result.configuredModel());
		assertTrue(result.message().contains(CONFIGURED_MODEL));

		verify(ollamaClient).getModels();
		verify(ollamaClient, never()).generate(anyString());
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("invalidModelLists")
	void shouldReportInvalidResponseWithoutClaimingModelIsMissing(
			String scenario,
			OllamaTagsResponse tagsResponse
	) {
		when(ollamaClient.getModels()).thenReturn(tagsResponse);

		AiProviderHealthResult result = service.getSetupHealthInfo();

		assertEquals(READY, result.backendStatus());
		assertEquals(READY, result.providerStatus());
		assertEquals(UNKNOWN, result.modelStatus());
		assertEquals(
				AiProviderHealthDiagnosticCode.INVALID_PROVIDER_RESPONSE,
				result.diagnosticCode(),
				scenario
		);
		assertEquals(CONFIGURED_MODEL, result.configuredModel());

		verify(ollamaClient, never()).generate(anyString());
	}

	@Test
	void shouldDistinguishTrialFailureFromMissingModel() {
		OllamaModelInfo modelInfo = mock(OllamaModelInfo.class);
		when(modelInfo.name()).thenReturn(CONFIGURED_MODEL);

		when(ollamaClient.getModels())
				.thenReturn(new OllamaTagsResponse(List.of(modelInfo)));
		when(ollamaClient.generate(anyString()))
				.thenThrow(new RestClientException("Trial generation failed"));

		AiProviderHealthResult result = service.getSetupHealthInfo();

		assertEquals(READY, result.providerStatus());
		assertEquals(FAILED, result.modelStatus());
		assertEquals(
				AiProviderHealthDiagnosticCode.MODEL_TRIAL_FAILED,
				result.diagnosticCode()
		);
		assertEquals(CONFIGURED_MODEL, result.configuredModel());

		verify(ollamaClient).generate(anyString());
	}
}
