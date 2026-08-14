package com.antonstrokov.jaide.plugin.service.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JaideImprovementValidationService {

	public boolean isNoOpImprovement(String originalCode, String improvedCode) {
		List<String> originalTokens = tokenizeCode(originalCode);
		List<String> improvedTokens = tokenizeCode(improvedCode);

		return !originalTokens.isEmpty() && originalTokens.equals(improvedTokens);
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

	private List<String> tokenizeCode(String code) {
		if (code == null || code.isBlank()) {
			return Collections.emptyList();
		}

		String noComments = code.replaceAll("//.*|/\\*[\\s\\S]*?\\*/", "");

		List<String> tokens = new ArrayList<>();
		Matcher matcher = Pattern.compile("\\w+|[^\\s\\w]").matcher(noComments);
		while (matcher.find()) {
			tokens.add(matcher.group());
		}

		return tokens;
	}

	public boolean hasSuspiciousChanges(String originalCode, String improvedCode, List<String> changes) {
		if (changes == null || changes.isEmpty() || improvedCode == null || originalCode == null) {
			return false;
		}

		boolean claimsRemoval = false;
		for (String change : changes) {
			if (change == null) {
				continue;
			}
			String lowerChange = change.toLowerCase();
			if ((lowerChange.contains("удален") || lowerChange.contains("removed") ||
					lowerChange.contains("удалил") || lowerChange.contains("deleted")) &&
					!lowerChange.contains("не ") && !lowerChange.contains("not ")) {
				claimsRemoval = true;
				break;
			}
		}

		if (claimsRemoval) {
			List<String> originalTokens = tokenizeCode(originalCode);
			List<String> improvedTokens = tokenizeCode(improvedCode);

			return Collections.indexOfSubList(improvedTokens, originalTokens) != -1;
		}

		return false;
	}
}
