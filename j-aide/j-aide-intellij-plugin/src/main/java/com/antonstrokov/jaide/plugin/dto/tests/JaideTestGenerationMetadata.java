package com.antonstrokov.jaide.plugin.dto.tests;

public record JaideTestGenerationMetadata(
		String traceId,
		String backendVersion,
		Long responseTimeMs,
		Boolean retried
) {
}
