package com.antonstrokov.j_aide.core.service;

import com.antonstrokov.j_aide.core.config.AiProperties;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthResult;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthStatus;
import org.springframework.stereotype.Service;

@Service
public class AiProviderHealthService {
	private final AiProperties aiProperties;

	public AiProviderHealthService(AiProperties aiProperties) {
		this.aiProperties = aiProperties;
	}

	public AiProviderHealthResult getHealthInfo() {
		return new AiProviderHealthResult(
				AiProviderHealthStatus.READY,
				AiProviderHealthStatus.UNKNOWN,
				AiProviderHealthStatus.UNKNOWN,
				null,
				null,
				"AI provider health check is not implemented yet."
		);
	}

	private String buildOllamaVersionUrl() {
		return aiProperties.ollama().baseUrl() + "/api/version";
	}
}
