package com.antonstrokov.j_aide.api.dto.tests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestGenerationResponse {
	private TestGenerationResult testResult;
	private String rawJson;
	private boolean success;
	private TestGenerationMetadata metadata;
}
