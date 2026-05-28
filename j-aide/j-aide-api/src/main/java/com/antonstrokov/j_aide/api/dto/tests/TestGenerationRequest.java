package com.antonstrokov.j_aide.api.dto.tests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestGenerationRequest {
	private String code;
	private String mode;
	private String language;
	private String fileName;
	private Integer lineStart;
	private Integer lineEnd;
	private String projectName;
	private String moduleName;
	private String pluginVersion;
	private String ideVersion;
}
