package com.antonstrokov.j_aide.core.integration.ollama;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.antonstrokov.j_aide.core.config.AiProperties;
import com.antonstrokov.j_aide.core.integration.ollama.dto.OllamaVersionResponse;

@Component
public class OllamaClient {
	private final RestClient restClient = RestClient.create();
	private final AiProperties aiProperties;

	public OllamaClient(AiProperties aiProperties) {
		this.aiProperties = aiProperties;
	}

	public OllamaVersionResponse getVersion() {
		return restClient.get()
				.uri(buildVersionUrl())
				.retrieve()
				.body(OllamaVersionResponse.class);
	}

	private String buildVersionUrl() {
		return aiProperties.ollama().baseUrl() + "/api/version";
	}
}