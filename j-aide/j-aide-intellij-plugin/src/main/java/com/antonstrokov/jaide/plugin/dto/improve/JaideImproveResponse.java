package com.antonstrokov.jaide.plugin.dto.improve;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JaideImproveResponse(
		JaideImprovement improvement,
		boolean success
) {
}