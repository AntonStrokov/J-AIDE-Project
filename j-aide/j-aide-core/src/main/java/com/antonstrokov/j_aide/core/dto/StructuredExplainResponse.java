package com.antonstrokov.j_aide.core.dto;

import lombok.Data;

@Data
public class StructuredExplainResponse {
	private String summary;
	private String details;
	private String complexity;
}