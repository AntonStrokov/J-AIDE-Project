package com.antonstrokov.jaide.plugin.language;

import java.util.Locale;

public class JaideLanguageResolver {

	public String resolve(String fileName) {
		String language = mapKnownLanguage(resolveExtension(fileName));
		return language == null ? "plain_text" : language;
	}

	public String resolve(String psiLanguageId, String fileName) {
		String language = mapKnownLanguage(psiLanguageId);
		return language == null ? resolve(fileName) : language;
	}

	private String mapKnownLanguage(String identifier) {
		if (identifier == null) {
			return null;
		}

		return switch (identifier.toLowerCase(Locale.ROOT)) {
			case "java" -> "java";
			case "kt", "kotlin" -> "kotlin";
			case "sql" -> "sql";
			case "xml" -> "xml";
			case "js", "javascript" -> "javascript";
			default -> null;
		};
	}

	private String resolveExtension(String fileName) {
		if (fileName == null || fileName.isBlank()) {
			return "";
		}

		int dotIndex = fileName.lastIndexOf('.');

		if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
			return "";
		}

		return fileName.substring(dotIndex + 1);
	}
}
