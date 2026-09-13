package com.antonstrokov.jaide.plugin.dto.improve;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JaideImproveChangeFact(
		String operation,
		String symbolKind,
		String before,
		String after
) {
}
