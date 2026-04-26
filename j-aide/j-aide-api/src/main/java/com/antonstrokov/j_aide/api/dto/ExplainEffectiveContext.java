package com.antonstrokov.j_aide.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExplainEffectiveContext {
	private String mode;
	private String language;
	private boolean supportedLanguage;
}