package com.antonstrokov.j_aide.api.dto.tests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestGenerationResult {
	private String summary;
	private String testCode;
	private String testFramework;
	private List<String> coveredScenarios;
	private String riskHint;
	private String confidence;
}
