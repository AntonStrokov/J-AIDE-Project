package com.antonstrokov.jaide.plugin.ui.health;

import com.antonstrokov.jaide.plugin.dto.health.JaideHealthResponse;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.antonstrokov.jaide.plugin.dto.health.JaideHealthStatus.FAILED;
import static com.antonstrokov.jaide.plugin.dto.health.JaideHealthStatus.READY;
import static com.antonstrokov.jaide.plugin.dto.health.JaideHealthStatus.UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaideAiHealthGuidanceBuilderTest {

	private static final String MODEL = "jaide-test-model:custom";

	private final JaideAiHealthGuidanceBuilder guidanceBuilder =
			new JaideAiHealthGuidanceBuilder();

	@Test
	void shouldBuildCommandForConfiguredModelRegardlessOfMessageWording() {
		JaideHealthResponse response = new JaideHealthResponse(
				READY,
				READY,
				FAILED,
				"test-version",
				12L,
				"Human-readable wording can change.",
				"MODEL_NOT_FOUND",
				MODEL
		);

		String guidance = guidanceBuilder.build(response);

		assertNotNull(guidance);
		assertTrue(guidance.contains("ollama pull " + MODEL));
		assertTrue(guidance.contains("Retry"));
	}

	@Test
	void shouldNotOfferDownloadAfterTrialFailure() {
		JaideHealthResponse response = healthResponse(
				READY,
				READY,
				FAILED,
				"MODEL_TRIAL_FAILED",
				MODEL
		);

		assertNull(guidanceBuilder.build(response));
	}

	@Test
	void shouldNotOfferDownloadForOtherOrMissingDiagnosticCodes() {
		String[] diagnosticCodes = {
				null,
				"NONE",
				"PROVIDER_UNREACHABLE",
				"INVALID_PROVIDER_RESPONSE",
				"FUTURE_DIAGNOSTIC_CODE"
		};

		for (String diagnosticCode : diagnosticCodes) {
			JaideHealthResponse response = healthResponse(
					READY,
					READY,
					FAILED,
					diagnosticCode,
					MODEL
			);

			assertNull(
					guidanceBuilder.build(response),
					"Unexpected guidance for code: " + diagnosticCode
			);
		}
	}

	@Test
	void shouldExplainMissingModelNameWithoutInventingCommand() {
		String[] modelNames = {null, "", " "};

		for (String modelName : modelNames) {
			JaideHealthResponse response = healthResponse(
					READY,
					READY,
					FAILED,
					"MODEL_NOT_FOUND",
					modelName
			);

			String guidance = guidanceBuilder.build(response);

			assertNotNull(guidance);
			assertTrue(guidance.contains("backend configuration"));
			assertFalse(guidance.contains("ollama pull"));
		}
	}

	@Test
	void shouldNotOfferDownloadForContradictoryStatuses() {
		List<JaideHealthResponse> responses = List.of(
				healthResponse(FAILED, READY, FAILED, "MODEL_NOT_FOUND", MODEL),
				healthResponse(READY, FAILED, FAILED, "MODEL_NOT_FOUND", MODEL),
				healthResponse(READY, READY, READY, "MODEL_NOT_FOUND", MODEL),
				healthResponse(READY, READY, UNKNOWN, "MODEL_NOT_FOUND", MODEL)
		);

		for (JaideHealthResponse response : responses) {
			assertNull(
					guidanceBuilder.build(response),
					response.toString()
			);
		}
	}

	@Test
	void shouldReturnNoGuidanceForMissingResponse() {
		assertNull(guidanceBuilder.build(null));
	}

	private JaideHealthResponse healthResponse(
			JaideHealthStatus backendStatus,
			JaideHealthStatus providerStatus,
			JaideHealthStatus modelStatus,
			String diagnosticCode,
			String configuredModel
	) {
		return new JaideHealthResponse(
				backendStatus,
				providerStatus,
				modelStatus,
				"test-version",
				12L,
				"Configured AI model is not available: " + configuredModel,
				diagnosticCode,
				configuredModel
		);
	}
}
