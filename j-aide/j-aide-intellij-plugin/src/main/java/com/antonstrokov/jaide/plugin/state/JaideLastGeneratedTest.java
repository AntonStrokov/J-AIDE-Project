package com.antonstrokov.jaide.plugin.state;

public record JaideLastGeneratedTest(
		String testCode,
		String fileName,
		String projectName,
		String moduleName,
		int lineStart,
		int lineEnd
) {
}
