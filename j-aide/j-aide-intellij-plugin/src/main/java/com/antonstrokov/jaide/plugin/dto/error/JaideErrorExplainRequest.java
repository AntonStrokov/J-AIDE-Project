package com.antonstrokov.jaide.plugin.dto.error;

public record JaideErrorExplainRequest(
		String errorText,
		String mode,
		String language,
		String fileName,
		Integer lineStart,
		Integer lineEnd,
		String projectName,
		String filePath,
		String ideVersion,
		String pluginVersion,
		String moduleName
) {
}