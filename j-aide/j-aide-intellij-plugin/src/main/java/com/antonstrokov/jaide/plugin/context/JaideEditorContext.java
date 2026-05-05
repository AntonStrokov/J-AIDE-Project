package com.antonstrokov.jaide.plugin.context;

public record JaideEditorContext(
		String selectedCode,
		String fileName,
		int lineStart,
		int lineEnd,
		String projectName,
		String moduleName,
		String ideVersion
) {
}