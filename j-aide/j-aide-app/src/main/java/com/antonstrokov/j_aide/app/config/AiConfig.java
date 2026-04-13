package com.antonstrokov.j_aide.app.config;

import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AiConfig {

	@Value("${j-aide.ai.ollama.base-url}")
	private String baseUrl;

	@Value("${j-aide.ai.ollama.model}")
	private String modelName;

	@Bean
	public OllamaChatModel ollamaChatModel() {
		return OllamaChatModel.builder()
				.baseUrl(baseUrl)
				.modelName(modelName)
				.timeout(Duration.ofSeconds(30))
				.build();
	}
}