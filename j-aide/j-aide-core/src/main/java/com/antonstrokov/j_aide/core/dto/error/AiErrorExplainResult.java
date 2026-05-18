package com.antonstrokov.j_aide.core.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiErrorExplainResult {
	private StructuredErrorExplanationResponse errorExplanation;
	private String rawJson;
	private String mode;
	private String language;
	private Boolean retried;
}