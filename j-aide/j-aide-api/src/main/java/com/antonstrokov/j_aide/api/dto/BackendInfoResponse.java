package com.antonstrokov.j_aide.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BackendInfoResponse {
	private String backendName;
	private String backendVersion;
	private String defaultMode;
	private String defaultLanguage;
	private String llmProvider;
	private String status;
	private List<String> supportedModes;
	private List<String> supportedLanguages;
	private List<String> supportedFeatures;

}