package com.antonstrokov.jaide.plugin.dto.explain;
import com.antonstrokov.jaide.plugin.model.JaideExplainMode;

public record JaideExplainRequest(
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