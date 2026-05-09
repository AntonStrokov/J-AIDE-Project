package com.antonstrokov.jaide.plugin.dto.improve;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JaideImprovement(
		String summary,
		String improvedCode,
		List<String> changes,
		String riskHint,
		String confidence
) {
}