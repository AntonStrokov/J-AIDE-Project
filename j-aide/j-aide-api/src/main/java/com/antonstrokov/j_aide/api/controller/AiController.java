package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.api.dto.ExplainRequest;
import com.antonstrokov.j_aide.api.dto.ExplainResponse;
import com.antonstrokov.j_aide.core.service.AiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {

	private final AiService aiService;

	public AiController(AiService aiService) {
		this.aiService = aiService;
	}

	@PostMapping("/ai/explain")
	public ExplainResponse explain(@RequestBody ExplainRequest request) {
		return new ExplainResponse(aiService.explain(request.getCode()));
	}
}