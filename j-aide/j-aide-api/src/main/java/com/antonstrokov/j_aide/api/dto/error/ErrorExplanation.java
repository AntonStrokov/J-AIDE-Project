package com.antonstrokov.j_aide.api.dto.error;

import lombok.Data;

import java.util.List;

@Data
public class ErrorExplanation {
	private String summary;
	private String likelyCause;
	private String whereToLook;
	private List<String> suggestedFixes;
	private String riskHint;
	private String confidence;
}