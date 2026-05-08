package com.antonstrokov.jaide.plugin.dto.backend;

public record JaideBackendExplainRequest(
		String code,
		String mode,
		String language,
		String fileName,
		int lineStart,
		int lineEnd,
		String projectName,
		String moduleName,
		String pluginVersion,
		String ideVersion
) {
}