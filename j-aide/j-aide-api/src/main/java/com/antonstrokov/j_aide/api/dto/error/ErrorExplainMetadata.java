package com.antonstrokov.j_aide.api.dto.error;

import lombok.Data;

@Data
public class ErrorExplainMetadata {
	private String traceId;
	private String backendVersion;
	private long responseTimeMs;
	private Boolean retried;
}