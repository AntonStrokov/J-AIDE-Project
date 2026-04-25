package com.antonstrokov.j_aide.api.dto;

import com.antonstrokov.j_aide.core.dto.StructuredExplainResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExplainResponse {
	private StructuredExplainResponse explanation;
	private String rawJson;
	private String mode;
	private String language;
	private String traceId;
	private boolean success;
	private boolean supportedLanguage;
	private String backendVersion;
	private String fileName;
	private String lineRange;
	private String projectName;
	private String moduleName;
	private Long responseTimeMs;
	private String requestLanguage;
	private String requestMode;
	private String requestFileName;
	private String requestProjectName;
	private String requestModuleName;
	private String requestPluginVersion;
	private String requestIdeVersion;
	private Boolean retried;
}