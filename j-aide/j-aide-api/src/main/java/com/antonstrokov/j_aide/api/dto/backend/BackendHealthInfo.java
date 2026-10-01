package com.antonstrokov.j_aide.api.dto.backend;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BackendHealthInfo {
	private BackendHealthStatus backendStatus;
	private BackendHealthStatus providerStatus;
	private BackendHealthStatus modelStatus;
	private String providerVersion;
	private Long responseTimeMs;
	private String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String diagnosticCode;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String configuredModel;
}
