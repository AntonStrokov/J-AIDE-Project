package com.antonstrokov.jaide.plugin.dto.error;

public record JaideErrorExplainMetadata(
		String traceId,
		String backendVersion,
		long responseTimeMs,
		Boolean retried
) {
}