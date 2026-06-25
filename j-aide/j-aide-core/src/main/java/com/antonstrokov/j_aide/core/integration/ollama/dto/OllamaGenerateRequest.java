package com.antonstrokov.j_aide.core.integration.ollama.dto;

public record OllamaGenerateRequest(
		String model,
		String prompt,
		boolean stream
) {
}