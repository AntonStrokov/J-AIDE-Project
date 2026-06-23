package com.antonstrokov.j_aide.core.dto.health;

public record AiProviderHealthResult(
		AiProviderHealthStatus backendStatus,
		AiProviderHealthStatus providerStatus,
		AiProviderHealthStatus modelStatus,
		String providerVersion,
		Long responseTimeMs,
		String message
) {
}