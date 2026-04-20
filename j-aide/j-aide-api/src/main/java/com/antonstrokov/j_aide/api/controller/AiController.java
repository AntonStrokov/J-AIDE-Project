package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.api.dto.ExplainRequest;
import com.antonstrokov.j_aide.api.dto.ExplainResponse;
import com.antonstrokov.j_aide.core.dto.AiExplainResult;
import com.antonstrokov.j_aide.core.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {

	private static final Logger log = LoggerFactory.getLogger(AiController.class);
	private final AiService aiService;

	public AiController(AiService aiService) {
		this.aiService = aiService;
	}

	@PostMapping("/ai/explain")
	public ExplainResponse explain(@RequestBody ExplainRequest request) {
		log.info("Received explain request, code length={}", request.getCode().length());

		AiExplainResult result = aiService.explain(
				request.getCode(),
				request.getMode(),
				request.getLanguage()
		);

		String traceId = MDC.get("traceId");

		boolean success = result.getRawJson() == null;

		return new ExplainResponse(
				result.getExplanation(),
				result.getRawJson(),
				result.getMode(),
				result.getLanguage(),
				traceId,
				success
		);
	}
}