package com.antonstrokov.j_aide.api.dto.error;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorExplainResponse {
	private ErrorExplanation errorExplanation;
	private String rawJson;
	private boolean success;
	private ErrorExplainMetadata metadata;
}