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
	private String requestLanguage;
	private String requestMode;
	private String requestFileName;
	private String requestProjectName;
	private String requestModuleName;
	private String requestPluginVersion;
	private String requestIdeVersion;

	private ExplainMetadata metadata;
	private ExplainEffectiveContext effectiveContext;
	private ExplainFileContext fileContext;
	private ExplainRequestContext requestContext;
}