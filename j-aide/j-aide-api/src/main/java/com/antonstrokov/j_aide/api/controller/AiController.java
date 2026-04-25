package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.api.dto.ExplainRequest;
import com.antonstrokov.j_aide.api.dto.ExplainResponse;
import com.antonstrokov.j_aide.core.config.AppProperties;
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
	private final AppProperties appProperties;

	public AiController(AiService aiService, AppProperties appProperties) {
		this.aiService = aiService;
		this.appProperties = appProperties;
	}

	@PostMapping("/ai/explain")
	public ExplainResponse explain(@RequestBody ExplainRequest request) {
		long startTime = System.currentTimeMillis();

		log.info("Received explain request, code length={}", request.getCode().length());

		log.info(
				"Explain context: language={}, mode={}, fileName={}, projectName={}, moduleName={}",
				request.getLanguage(),
				request.getMode(),
				request.getFileName(),
				request.getProjectName(),
				request.getModuleName()
		);

		AiExplainResult result = aiService.explain(
				request.getCode(),
				request.getMode(),
				request.getLanguage(),
				request.getFileName(),
				request.getLineStart(),
				request.getLineEnd(),
				request.getProjectName(),
				request.getModuleName(),
				request.getPluginVersion(),
				request.getIdeVersion()
		);

		long responseTimeMs = System.currentTimeMillis() - startTime;

		String traceId = MDC.get("traceId");

		boolean success = result.getRawJson() == null;

		boolean supportedLanguage = !"plain_text".equals(result.getLanguage());

		log.info(
				"Explain result: complexity={}, confidence={}, success={}, responseTimeMs={}",
				result.getExplanation().getComplexity(),
				result.getExplanation().getConfidence(),
				success,
				responseTimeMs
		);

		String backendVersion = appProperties.version();

		String lineRange = (request.getLineStart() != null && request.getLineEnd() != null)
				? request.getLineStart() + "-" + request.getLineEnd()
				: null;



		return new ExplainResponse(
				result.getExplanation(),
				result.getRawJson(),
				result.getMode(),
				result.getLanguage(),
				traceId,
				success,
				supportedLanguage,
				backendVersion,
				request.getFileName(),
				lineRange,
				request.getProjectName(),
				request.getModuleName(),
				responseTimeMs,
				request.getLanguage(),
				request.getMode(),
				request.getFileName(),
				request.getProjectName(),
				request.getModuleName(),
				request.getPluginVersion(),
				request.getIdeVersion(),
				result.getRetried()
		);
	}
}