package com.antonstrokov.j_aide.api.dto.explain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExplainMetadata {
	private String traceId;
	private String backendVersion;
	private Long responseTimeMs;
	private Boolean retried;
}