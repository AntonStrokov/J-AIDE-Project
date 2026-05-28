package com.antonstrokov.jaide.plugin.dto.tests;

import java.util.List;

public record JaideTestGenerationResult(
		String summary,
		String testCode,
		String testFramework,
		List<String> coveredScenarios,
		String riskHint,
		String confidence
) {
}
