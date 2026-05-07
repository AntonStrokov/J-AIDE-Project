package com.antonstrokov.j_aide.api.dto.improve;

import lombok.Data;

@Data
public class ImproveRequest {
	private String code;
	private String mode;
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