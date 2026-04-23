package com.antonstrokov.j_aide.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StructuredExplainResponse {
	private String summary;
	private String details;
	private String complexity;
	private String suggestion;
	private String bestPractice;
	private String riskHint;
	private String confidence;
	private String codeSmell;
	private String inputType;
}