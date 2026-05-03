package com.antonstrokov.jaide.plugin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JaideExplainResponse(
		JaideExplanation explanation,
		boolean success
) {
}