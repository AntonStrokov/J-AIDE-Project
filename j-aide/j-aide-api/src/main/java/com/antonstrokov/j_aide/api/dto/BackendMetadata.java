package com.antonstrokov.j_aide.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BackendMetadata {
	private String backendName;
	private String backendVersion;
	private String status;
}