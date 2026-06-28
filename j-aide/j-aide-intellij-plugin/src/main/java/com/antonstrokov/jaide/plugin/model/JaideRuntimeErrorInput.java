package com.antonstrokov.jaide.plugin.model;

public record JaideRuntimeErrorInput(
		String errorText,
		String fileName,
		Integer lineStart,
		Integer lineEnd,
		String projectName,
		String ideVersion,
		String moduleName,
		JaideRuntimeErrorInputSource source
) {
}