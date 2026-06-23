package com.antonstrokov.j_aide.core.service;

import com.antonstrokov.j_aide.core.config.AiProperties;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthResult;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthStatus;
import com.antonstrokov.j_aide.core.integration.ollama.OllamaClient;
import com.antonstrokov.j_aide.core.integration.ollama.dto.OllamaTagsResponse;
import org.springframework.stereotype.Service;

@Service
public class AiProviderHealthService {
	private final OllamaClient ollamaClient;
	private final AiProperties aiProperties;

	public AiProviderHealthService(
			OllamaClient ollamaClient,
			AiProperties aiProperties
	) {
		this.ollamaClient = ollamaClient;
		this.aiProperties = aiProperties;
	}

	public AiProviderHealthResult getHealthInfo() {
		long startedAt = System.currentTimeMillis();

		var versionResponse = ollamaClient.getVersion();
		OllamaTagsResponse tagsResponse = ollamaClient.getModels();

		boolean configuredModelAvailable = isConfiguredModelAvailable(tagsResponse);
		long responseTimeMs = System.currentTimeMillis() - startedAt;

		return new AiProviderHealthResult(
				AiProviderHealthStatus.READY,
				AiProviderHealthStatus.READY,
				configuredModelAvailable
						? AiProviderHealthStatus.READY
						: AiProviderHealthStatus.FAILED,
				versionResponse == null ? null : versionResponse.version(),
				responseTimeMs,
				configuredModelAvailable
						? "AI provider and configured model are available."
						: "Configured AI model is not available: " + aiProperties.ollama().model()
		);
	}

	private boolean isConfiguredModelAvailable(OllamaTagsResponse tagsResponse) {
		if (tagsResponse == null || tagsResponse.models() == null) {
			return false;
		}

		String configuredModel = aiProperties.ollama().model();

		return tagsResponse.models().stream()
				.anyMatch(modelInfo -> modelInfo != null
						&& (configuredModel.equals(modelInfo.name())
						|| configuredModel.equals(modelInfo.model())));
	}
}
