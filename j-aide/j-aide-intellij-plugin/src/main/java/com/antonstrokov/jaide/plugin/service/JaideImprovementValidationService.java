package com.antonstrokov.jaide.plugin.service;

import java.util.List;

public class JaideImprovementValidationService {

	public boolean isNoOpImprovement(String originalCode, String improvedCode) {
		String normalizedOriginalCode = normalizeCode(originalCode);
		String normalizedImprovedCode = normalizeCode(improvedCode);

		return !normalizedOriginalCode.isBlank()
				&& normalizedOriginalCode.equals(normalizedImprovedCode);
	}

	public boolean hasMarkdownCodeFence(String improvedCode) {
		if (improvedCode == null) {
			return false;
		}

		return improvedCode.contains("```");
	}

	public boolean isBlankImprovedCode(String improvedCode) {
		return improvedCode == null || improvedCode.isBlank();
	}

	public boolean hasNoChangeDescriptions(List<String> changes) {
		if (changes == null || changes.isEmpty()) {
			return true;
		}

		return changes.stream()
				.allMatch(change -> change == null || change.isBlank());
	}

	private String normalizeCode(String code) {
		if (code == null) {
			return "";
		}

		return code
				.replace("\r\n", "\n")
				.replace("\r", "\n")
				.trim();
	}
}