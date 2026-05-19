package com.antonstrokov.jaide.plugin.dto.error;

public record JaideErrorExplainResponse(
		JaideErrorExplanation errorExplanation,
		String rawJson,
		boolean success,
		JaideErrorExplainMetadata metadata
) {
}