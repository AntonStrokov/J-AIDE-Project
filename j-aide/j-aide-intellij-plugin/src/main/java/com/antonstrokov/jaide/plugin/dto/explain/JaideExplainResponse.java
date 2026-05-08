package com.antonstrokov.jaide.plugin.dto.explain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JaideExplainResponse(
		JaideExplanation explanation,
		boolean success
) {
}