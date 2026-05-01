package com.antonstrokov.j_aide.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BackendLlmInfo {
	private String llmProvider;
	private String llmModel;
}
