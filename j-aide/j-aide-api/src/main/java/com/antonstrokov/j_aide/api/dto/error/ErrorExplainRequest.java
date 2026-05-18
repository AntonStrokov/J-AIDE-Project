package com.antonstrokov.j_aide.api.dto.error;

import lombok.Data;

@Data
public class ErrorExplainRequest {
	private String errorText;
	private String language;
	private String fileName;
	private Integer lineStart;
	private Integer lineEnd;
	private String projectName;
	private String filePath;
	private String ideVersion;
	private String pluginVersion;
	private String moduleName;
}