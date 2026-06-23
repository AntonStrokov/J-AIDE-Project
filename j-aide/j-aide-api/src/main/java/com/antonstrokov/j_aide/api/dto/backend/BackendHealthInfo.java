package com.antonstrokov.j_aide.api.dto.backend;

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
}
