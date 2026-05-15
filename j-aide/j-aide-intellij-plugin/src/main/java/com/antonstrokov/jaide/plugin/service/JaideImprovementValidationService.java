package com.antonstrokov.jaide.plugin.service;

public class JaideImprovementValidationService {

	public boolean isNoOpImprovement(String originalCode, String improvedCode) {
		String normalizedOriginalCode = normalizeCode(originalCode);
		String normalizedImprovedCode = normalizeCode(improvedCode);

		return !normalizedOriginalCode.isBlank()
				&& normalizedOriginalCode.equals(normalizedImprovedCode);
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