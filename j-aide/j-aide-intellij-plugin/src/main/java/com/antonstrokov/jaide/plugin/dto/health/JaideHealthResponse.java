package com.antonstrokov.jaide.plugin.dto.health;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JaideHealthResponse(
		JaideHealthStatus backendStatus,
		JaideHealthStatus providerStatus,
		JaideHealthStatus modelStatus,
		String providerVersion,
		Long responseTimeMs,
		String message,
		String diagnosticCode,
		String configuredModel
) {
}
