package com.antonstrokov.jaide.plugin.dto.tests;

public record JaideTestGenerationResponse(
		JaideTestGenerationResult testResult,
		String rawJson,
		boolean success
) {
}
