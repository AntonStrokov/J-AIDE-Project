package com.antonstrokov.j_aide.core.service;

import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthResult;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthStatus;
import com.antonstrokov.j_aide.core.integration.ollama.OllamaClient;
import org.springframework.stereotype.Service;

@Service
public class AiProviderHealthService {
	private final OllamaClient ollamaClient;

	public AiProviderHealthService(OllamaClient ollamaClient) {
		this.ollamaClient = ollamaClient;
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
}
