package com.antonstrokov.jaide.plugin.language;

public class JaideLanguageResolver {

	public String resolve(String fileName) {
		String extension = resolveExtension(fileName);

		return switch (extension) {
			case "java" -> "java";
			case "kt" -> "kotlin";
			case "sql" -> "sql";
			case "xml" -> "xml";
			case "js" -> "javascript";
			default -> "plain_text";
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

		return fileName.substring(dotIndex + 1).toLowerCase();
	}
}