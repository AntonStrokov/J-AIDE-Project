package com.antonstrokov.j_aide.core.integration.ollama.dto;

import java.util.List;

public record OllamaTagsResponse(
		List<OllamaModelInfo> models
) {
}