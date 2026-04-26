package com.antonstrokov.j_aide.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExplainFileContext {
	private String fileName;
	private String lineRange;
	private String projectName;
	private String moduleName;
}