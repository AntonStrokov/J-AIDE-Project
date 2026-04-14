package com.antonstrokov.j_aide.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "j-aide.ai")
public record AiProperties(
		Ollama ollama,
		Limits limits
) {
	public record Ollama(
			String baseUrl,
			String model
	) {}

	public record Limits(
			int codeMaxLength
	) {}
}