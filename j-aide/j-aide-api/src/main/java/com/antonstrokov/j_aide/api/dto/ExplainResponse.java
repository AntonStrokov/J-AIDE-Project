package com.antonstrokov.j_aide.api.dto;

import com.antonstrokov.j_aide.core.dto.StructuredExplainResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExplainResponse {

	private StructuredExplainResponse explanation;
	private String rawJson;
	private boolean success;

	private ExplainMetadata metadata;
	private ExplainEffectiveContext effectiveContext;
	private ExplainFileContext fileContext;
	private ExplainRequestContext requestContext;
}