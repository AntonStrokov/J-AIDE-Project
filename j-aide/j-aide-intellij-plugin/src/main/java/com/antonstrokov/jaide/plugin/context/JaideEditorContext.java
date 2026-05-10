package com.antonstrokov.jaide.plugin.context;

public record JaideEditorContext(
		String selectedCode,
		String fileName,
		int lineStart,
		int lineEnd,
		int selectionStart,
		int selectionEnd,
		String projectName,
		String moduleName,
		String ideVersion
) {
}