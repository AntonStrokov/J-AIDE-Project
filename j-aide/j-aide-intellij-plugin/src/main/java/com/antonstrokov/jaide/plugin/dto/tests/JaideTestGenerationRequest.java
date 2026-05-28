package com.antonstrokov.jaide.plugin.dto.tests;

import com.antonstrokov.jaide.plugin.model.JaideExplainMode;

public record JaideTestGenerationRequest(
		String code,
		JaideExplainMode mode,
		String fileName,
		Integer lineStart,
		Integer lineEnd,
		String projectName,
		String moduleName,
		String ideVersion
) {
}
