package com.antonstrokov.j_aide.core.dto.improve;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiImproveResult {
	private StructuredImproveResponse improvement;
	private String rawJson;
	private String mode;
	private String language;
	private Boolean retried;
}