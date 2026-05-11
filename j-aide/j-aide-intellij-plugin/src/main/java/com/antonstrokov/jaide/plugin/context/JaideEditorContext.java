package com.antonstrokov.jaide.plugin.context;

import com.intellij.openapi.editor.Document;

public record JaideEditorContext(
		String selectedCode,
		String fileName,
		int lineStart,
		int lineEnd,
		int selectionStart,
		int selectionEnd,
		String projectName,
		String moduleName,
		String ideVersion,
		Document document
) {
}