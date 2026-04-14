package com.antonstrokov.j_aide.api.dto;

import com.antonstrokov.j_aide.core.dto.StructuredExplainResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExplainResponse {
	private StructuredExplainResponse explanation;
	private String rawJson;
}