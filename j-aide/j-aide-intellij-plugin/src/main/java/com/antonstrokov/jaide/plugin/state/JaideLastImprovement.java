package com.antonstrokov.jaide.plugin.state;

import com.intellij.openapi.editor.Document;

public record JaideLastImprovement(
		String originalCode,
		String improvedCode,
		String fileName,
		String projectName,
		String moduleName,
		int lineStart,
		int lineEnd,
		int selectionStart,
		int selectionEnd,
		Document document
) {
}