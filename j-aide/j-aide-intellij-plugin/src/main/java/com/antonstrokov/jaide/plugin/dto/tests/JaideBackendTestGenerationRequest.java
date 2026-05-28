package com.antonstrokov.jaide.plugin.dto.tests;

public record JaideBackendTestGenerationRequest(
		String code,
		String mode,
		String language,
		String fileName,
		Integer lineStart,
		Integer lineEnd,
		String projectName,
		String moduleName,
		String pluginVersion,
		String ideVersion
) {
}
