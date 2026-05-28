package com.antonstrokov.j_aide.core.dto.tests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StructuredTestGenerationResponse {
	private String summary;
	private String testCode;
	private String testFramework;
	private List<String> coveredScenarios;
	private String riskHint;
	private String confidence;
}
