package com.antonstrokov.j_aide.api.dto.improve;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ImproveResult {
	private String summary;
	private String improvedCode;
	private List<String> changes;
	private String riskHint;
	private String confidence;
}