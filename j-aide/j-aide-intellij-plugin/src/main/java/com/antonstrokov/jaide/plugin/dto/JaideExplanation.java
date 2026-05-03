package com.antonstrokov.jaide.plugin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JaideExplanation(
		String summary,
		String details,
		String complexity,
		String suggestion,
		String bestPractice,
		String riskHint,
		String confidence,
		String codeSmell,
		String inputType
) {
}