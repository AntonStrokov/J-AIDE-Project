package com.antonstrokov.j_aide.api.dto;

import lombok.Data;

@Data
public class ExplainRequest {
	private String code;
	private String mode;
}