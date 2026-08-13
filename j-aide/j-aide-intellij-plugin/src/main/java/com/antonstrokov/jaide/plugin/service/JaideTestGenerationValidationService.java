package com.antonstrokov.jaide.plugin.service;

public class JaideTestGenerationValidationService {

	public boolean hasMarkdownCodeFence(String testCode) {
		if (testCode == null) {
			return false;
		}
		return testCode.contains("```");
	}

	public boolean isBlankTestCode(String testCode) {
		return testCode == null || testCode.isBlank();
	}

	public boolean isMissingClassDeclaration(String testCode) {
		if (testCode == null) {
			return true;
		}

		return !testCode.contains("class ");
	}
}
