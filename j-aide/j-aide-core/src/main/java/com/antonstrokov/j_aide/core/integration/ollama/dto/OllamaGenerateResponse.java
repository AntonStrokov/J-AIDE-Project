package com.antonstrokov.j_aide.core.integration.ollama.dto;

public record OllamaGenerateResponse(
		String response,
		Boolean done
) {
}