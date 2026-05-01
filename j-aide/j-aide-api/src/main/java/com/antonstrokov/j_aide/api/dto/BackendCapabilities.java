package com.antonstrokov.j_aide.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BackendCapabilities {
	private List<String> supportedModes;
	private List<String> supportedLanguages;
	private List<String> supportedFeatures;
}