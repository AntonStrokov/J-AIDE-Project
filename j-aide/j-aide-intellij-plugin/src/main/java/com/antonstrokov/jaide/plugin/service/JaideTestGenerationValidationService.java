package com.antonstrokov.jaide.plugin.service;

public class JaideTestGenerationValidationService {

	public boolean isBlankTestCode(String testCode) {
		return testCode == null || testCode.isBlank();
	}

	public boolean hasMarkdownCodeFence(String testCode) {
		return testCode != null && testCode.contains("```");
	}
}
