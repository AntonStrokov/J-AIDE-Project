package com.antonstrokov.j_aide.core.integration.ollama;

import com.antonstrokov.j_aide.core.config.AiProperties;
import com.antonstrokov.j_aide.core.integration.ollama.dto.OllamaTagsResponse;
import com.antonstrokov.j_aide.core.integration.ollama.dto.OllamaVersionResponse;
import com.antonstrokov.j_aide.core.integration.ollama.dto.OllamaGenerateRequest;
import com.antonstrokov.j_aide.core.integration.ollama.dto.OllamaGenerateResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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

	public OllamaTagsResponse getModels() {
		return restClient.get()
				.uri(buildTagsUrl())
				.retrieve()
				.body(OllamaTagsResponse.class);
	}

	private String buildTagsUrl() {
		return aiProperties.ollama().baseUrl() + "/api/tags";
	}

	public OllamaGenerateResponse generate(String prompt) {
		OllamaGenerateRequest request = new OllamaGenerateRequest(
				aiProperties.ollama().model(),
				prompt,
				false
		);

		return restClient.post()
				.uri(buildGenerateUrl())
				.body(request)
				.retrieve()
				.body(OllamaGenerateResponse.class);
	}

	private String buildGenerateUrl() {
		return aiProperties.ollama().baseUrl() + "/api/generate";
	}
}