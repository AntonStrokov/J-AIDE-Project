package com.antonstrokov.j_aide.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BackendInfoResponse {
	private String backendVersion;
	private List<String> supportedModes;
	private List<String> supportedLanguages;
}