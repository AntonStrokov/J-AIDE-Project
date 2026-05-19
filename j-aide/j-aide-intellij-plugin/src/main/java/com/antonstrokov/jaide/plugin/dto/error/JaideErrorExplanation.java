package com.antonstrokov.jaide.plugin.dto.error;

import java.util.List;

public record JaideErrorExplanation(
		String summary,
		String likelyCause,
		String whereToLook,
		List<String> suggestedFixes,
		String riskHint,
		String confidence
) {
}