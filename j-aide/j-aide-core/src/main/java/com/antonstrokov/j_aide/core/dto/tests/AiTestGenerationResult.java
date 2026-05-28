package com.antonstrokov.j_aide.core.dto.tests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiTestGenerationResult {
	private StructuredTestGenerationResponse testResult;
	private String rawJson;
	private String mode;
	private String language;
	private Boolean retried;

	public AiTestGenerationResult(
			StructuredTestGenerationResponse testResult,
			String rawJson,
			String mode,
			String language,
			Boolean retried
	) {
		this.testResult = testResult;
		this.rawJson = rawJson;
		this.mode = mode;
		this.language = language;
		this.retried = retried;
	}
}
