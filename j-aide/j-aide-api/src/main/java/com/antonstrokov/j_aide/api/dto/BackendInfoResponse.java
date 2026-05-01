package com.antonstrokov.j_aide.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BackendInfoResponse {

	private BackendMetadata metadata;
	private BackendDefaults defaults;
	private BackendLlmInfo llmInfo;
	private BackendEndpoints endpoints;
	private BackendCapabilities capabilities;

//	private String backendName;
//	private String backendVersion;
//	private String defaultMode;
//	private String defaultLanguage;
	private String llmProvider;
	private String llmModel;
//	private String status;
	private String explainEndpoint;
	private List<String> supportedModes;
	private List<String> supportedLanguages;
	private List<String> supportedFeatures;

}