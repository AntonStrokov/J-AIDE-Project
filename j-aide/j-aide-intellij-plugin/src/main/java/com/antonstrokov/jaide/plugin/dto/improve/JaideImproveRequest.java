package com.antonstrokov.jaide.plugin.dto.improve;

import com.antonstrokov.jaide.plugin.model.JaideExplainMode;

public record JaideImproveRequest(
		String code,
		JaideExplainMode mode,
		String fileName,
		int lineStart,
		int lineEnd,
		String projectName,
		String moduleName,
		String ideVersion
) {
}