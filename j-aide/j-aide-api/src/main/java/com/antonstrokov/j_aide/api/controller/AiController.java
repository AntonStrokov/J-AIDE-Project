package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.api.dto.ExplainRequest;
import com.antonstrokov.j_aide.api.dto.ExplainResponse;
import com.antonstrokov.j_aide.core.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {

	private final AiService aiService;
	private static final Logger log = LoggerFactory.getLogger(AiController.class);

	public AiController(AiService aiService) {
		this.aiService = aiService;
	}

	@PostMapping("/ai/explain")
	public ExplainResponse explain(@RequestBody ExplainRequest request) {
		log.info("Received explain request, code length={}", request.getCode().length());
		return new ExplainResponse(aiService.explain(request.getCode(), request.getMode()), null);
	}
}