package com.antonstrokov.j_aide.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiExplainResult {
	private StructuredExplainResponse explanation;
	private String rawJson;
	private String mode;
	private String language;
}