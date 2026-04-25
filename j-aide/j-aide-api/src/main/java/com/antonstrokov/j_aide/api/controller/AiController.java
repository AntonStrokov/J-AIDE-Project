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

		logExplainContext(request);

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

		String traceId = getTraceId();

		boolean success = isSuccess(result);

		boolean supportedLanguage = isSupportedLanguage(result);

		logExplainResult(result, success, responseTimeMs);

		String backendVersion = getBackendVersion();

		String lineRange = buildLineRange(request);

		return buildExplainResponse(
				request,
				result,
				traceId,
				success,
				supportedLanguage,
				backendVersion,
				lineRange,
				responseTimeMs
		);
	}

	private ExplainResponse buildExplainResponse(
			ExplainRequest request,
			AiExplainResult result,
			String traceId,
			boolean success,
			boolean supportedLanguage,
			String backendVersion,
			String lineRange,
			long responseTimeMs
	) {
		ExplainResponse response = new ExplainResponse();

		response.setExplanation(result.getExplanation());
		response.setRawJson(result.getRawJson());
		response.setMode(result.getMode());
		response.setLanguage(result.getLanguage());
		response.setTraceId(traceId);
		response.setSuccess(success);
		response.setSupportedLanguage(supportedLanguage);
		response.setBackendVersion(backendVersion);
		response.setFileName(request.getFileName());
		response.setLineRange(lineRange);
		response.setProjectName(request.getProjectName());
		response.setModuleName(request.getModuleName());
		response.setResponseTimeMs(responseTimeMs);
		response.setRequestLanguage(request.getLanguage());
		response.setRequestMode(request.getMode());
		response.setRequestFileName(request.getFileName());
		response.setRequestProjectName(request.getProjectName());
		response.setRequestModuleName(request.getModuleName());
		response.setRequestPluginVersion(request.getPluginVersion());
		response.setRequestIdeVersion(request.getIdeVersion());
		response.setRetried(result.getRetried());

		return response;
	}

	private String buildLineRange(ExplainRequest request) {
		return (request.getLineStart() != null && request.getLineEnd() != null)
				? request.getLineStart() + "-" + request.getLineEnd()
				: null;
	}

	private boolean isSuccess(AiExplainResult result) {
		return result.getRawJson() == null;
	}

	private boolean isSupportedLanguage(AiExplainResult result) {
		return !"plain_text".equals(result.getLanguage());
	}

	private String getBackendVersion() {
		return appProperties.version();
	}

	private String getTraceId() {
		return MDC.get("traceId");
	}

	private void logExplainContext(ExplainRequest request) {
		log.info(
				"Explain context: language={}, mode={}, fileName={}, projectName={}, moduleName={}",
				request.getLanguage(),
				request.getMode(),
				request.getFileName(),
				request.getProjectName(),
				request.getModuleName()
		);
	}

	private void logExplainResult(AiExplainResult result, boolean success, long responseTimeMs) {
		log.info(
				"Explain result: complexity={}, confidence={}, success={}, responseTimeMs={}",
				result.getExplanation().getComplexity(),
				result.getExplanation().getConfidence(),
				success,
				responseTimeMs
		);
	}
}