package com.antonstrokov.jaide.plugin.dto.improve;

public record JaideBackendImproveRequest(
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