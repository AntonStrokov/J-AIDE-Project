package com.antonstrokov.j_aide.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BackendInfoResponse {
	private BackendMetadata metadata;
	private BackendDefaults defaults;
	private BackendLlmInfo llmInfo;
	private BackendEndpoints endpoints;
	private BackendCapabilities capabilities;
}