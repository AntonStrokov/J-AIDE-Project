package com.antonstrokov.j_aide.api.dto.improve;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImproveMetadata {
	private String traceId;
	private String backendVersion;
	private Long responseTimeMs;
	private Boolean retried;
}