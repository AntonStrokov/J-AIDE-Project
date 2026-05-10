package com.antonstrokov.jaide.plugin.state;

public record JaideLastImprovement(
		String originalCode,
		String improvedCode,
		String fileName,
		String projectName,
		String moduleName,
		int lineStart,
		int lineEnd
) {
}