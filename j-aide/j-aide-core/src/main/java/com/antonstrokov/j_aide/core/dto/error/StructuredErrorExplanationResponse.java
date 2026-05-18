package com.antonstrokov.j_aide.core.dto.error;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class StructuredErrorExplanationResponse {
	private String summary;
	private String likelyCause;
	private String whereToLook;
	private List<String> suggestedFixes;
	private String riskHint;
	private String confidence;
}