package com.antonstrokov.j_aide.app.config;

import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AiConfig {

	private final com.antonstrokov.j_aide.core.config.AiProperties aiProperties;

	public AiConfig(com.antonstrokov.j_aide.core.config.AiProperties aiProperties) {
		this.aiProperties = aiProperties;
	}

	@Bean
	public OllamaChatModel ollamaChatModel() {
		return OllamaChatModel.builder()
				.baseUrl(aiProperties.ollama().baseUrl())
				.modelName(aiProperties.ollama().model())
				.timeout(Duration.ofSeconds(60))
				.build();
	}
}