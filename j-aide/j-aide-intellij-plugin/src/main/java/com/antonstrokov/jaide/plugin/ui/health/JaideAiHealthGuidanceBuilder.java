package com.antonstrokov.jaide.plugin.ui.health;

import com.antonstrokov.jaide.plugin.config.JaideUiLabels;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthResponse;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthStatus;

public final class JaideAiHealthGuidanceBuilder {

	private static final String MODEL_NOT_FOUND = "MODEL_NOT_FOUND";

	public String build(JaideHealthResponse response) {
		if (response == null
				|| response.backendStatus() != JaideHealthStatus.READY
				|| response.providerStatus() != JaideHealthStatus.READY
				|| response.modelStatus() != JaideHealthStatus.FAILED
				|| !MODEL_NOT_FOUND.equals(response.diagnosticCode())) {
			return null;
		}

		String configuredModel = response.configuredModel();

		if (configuredModel == null || configuredModel.isBlank()) {
			return JaideUiLabels.AI_MODEL_NOT_FOUND_WITHOUT_NAME_GUIDANCE;
		}

		return JaideUiLabels.AI_MODEL_NOT_FOUND_GUIDANCE
				.formatted(configuredModel);
	}
}
