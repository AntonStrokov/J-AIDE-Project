package com.antonstrokov.jaide.plugin.dto;

public record JaideExplainRequest(
		String code,
		String fileName,
		int lineStart,
		int lineEnd,
		String projectName,
		String moduleName,
		String ideVersion
) {
}