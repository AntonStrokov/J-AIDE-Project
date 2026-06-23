package com.antonstrokov.j_aide.core.service;

import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthResult;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthStatus;
import com.antonstrokov.j_aide.core.integration.ollama.OllamaClient;
import com.antonstrokov.j_aide.core.config.AiProperties;
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

		long responseTimeMs = System.currentTimeMillis() - startedAt;

		return new AiProviderHealthResult(
				AiProviderHealthStatus.READY,
				AiProviderHealthStatus.READY,
				AiProviderHealthStatus.UNKNOWN,
				versionResponse == null ? null : versionResponse.version(),
				responseTimeMs,
				"AI provider is reachable. Model health check is not implemented yet."
		);
	}
}
