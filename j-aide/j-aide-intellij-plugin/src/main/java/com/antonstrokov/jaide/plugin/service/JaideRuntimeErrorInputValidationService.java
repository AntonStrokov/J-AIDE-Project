package com.antonstrokov.jaide.plugin.service;

public class JaideRuntimeErrorInputValidationService {

	public boolean looksLikeErrorText(String text) {
		if (text == null || text.isBlank()) {
			return false;
		}

		String normalizedText = text.toLowerCase();

		return normalizedText.contains("exception")
				|| normalizedText.contains("error")
				|| normalizedText.contains("caused by:")
				|| normalizedText.contains("at ")
				|| normalizedText.contains("build failed")
				|| normalizedText.contains("compilation error")
				|| normalizedText.contains("failed to start")
				|| normalizedText.contains("port already in use")
				|| normalizedText.contains("connection refused")
				|| normalizedText.contains("beancreationexception")
				|| normalizedText.contains("nullpointerexception")
				|| normalizedText.contains("illegalargumentexception")
				|| normalizedText.contains("sqlexception")
				|| normalizedText.contains("unsatisfieddependencyexception")
				|| normalizedText.contains("application run failed")
				|| normalizedText.contains("cannot find symbol")
				|| normalizedText.contains("cannot resolve symbol")
				|| normalizedText.contains("class, interface, enum, or record expected")
				|| normalizedText.contains("illegal character")
				|| normalizedText.contains("illegal unicode escape")
				|| normalizedText.contains("preview feature")
				|| normalizedText.contains("disabled by default")
				|| normalizedText.contains("enable-preview")
				|| normalizedText.contains("source option")
				|| normalizedText.contains("target option")
				|| normalizedText.contains("release version")
				|| normalizedText.contains("package does not exist")
				|| normalizedText.contains("method does not override")
				|| normalizedText.contains("incompatible types")
				|| normalizedText.contains("symbol:")
				|| normalizedText.contains("location:");
	}
}