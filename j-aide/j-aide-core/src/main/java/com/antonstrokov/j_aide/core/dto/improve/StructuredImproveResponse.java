package com.antonstrokov.j_aide.core.dto.improve;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class StructuredImproveResponse {
	private String summary;
	private String improvedCode;
	private List<String> changes;
	private String riskHint;
	private String confidence;
}