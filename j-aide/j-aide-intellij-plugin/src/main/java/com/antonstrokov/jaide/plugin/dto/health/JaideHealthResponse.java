package com.antonstrokov.jaide.plugin.dto.health;

public record JaideHealthResponse(
		JaideHealthStatus backendStatus,
		JaideHealthStatus providerStatus,
		JaideHealthStatus modelStatus,
		String providerVersion,
		Long responseTimeMs,
		String message
) {
}