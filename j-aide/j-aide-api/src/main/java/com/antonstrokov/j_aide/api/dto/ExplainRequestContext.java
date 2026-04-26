package com.antonstrokov.j_aide.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExplainRequestContext {
	private String requestLanguage;
	private String requestMode;
	private String requestFileName;
	private String requestProjectName;
	private String requestModuleName;
	private String requestPluginVersion;
	private String requestIdeVersion;
}