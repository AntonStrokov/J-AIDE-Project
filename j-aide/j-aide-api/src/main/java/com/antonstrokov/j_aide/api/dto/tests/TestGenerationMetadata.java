package com.antonstrokov.j_aide.api.dto.tests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestGenerationMetadata {
	private String traceId;
	private String backendVersion;
	private Long responseTimeMs;
	private Boolean retried;
}
