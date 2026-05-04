package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.api.dto.*;
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

		long responseTimeMs = calculateResponseTimeMs(startTime);

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
		response.setSuccess(success);

		ExplainMetadata metadata = buildMetadata(
				traceId,
				backendVersion,
				responseTimeMs,
				result.getRetried()
		);

		response.setMetadata(metadata);

		ExplainEffectiveContext effectiveContext = buildEffectiveContext(
				result,
				supportedLanguage
		);

		response.setEffectiveContext(effectiveContext);

		ExplainFileContext fileContext = buildFileContext(request, lineRange);
		response.setFileContext(fileContext);

		ExplainRequestContext requestContext = buildRequestContext(request);
		response.setRequestContext(requestContext);

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
				"Explain context: language={}, mode={}, fileName={}, lineStart={}, lineEnd={}, projectName={}, moduleName={}",
				request.getLanguage(),
				request.getMode(),
				request.getFileName(),
				request.getLineStart(),
				request.getLineEnd(),
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

	private long calculateResponseTimeMs(long startTime) {
		return System.currentTimeMillis() - startTime;
	}

	private ExplainMetadata buildMetadata(
			String traceId,
			String backendVersion,
			long responseTimeMs,
			Boolean retried
	) {
		ExplainMetadata metadata = new ExplainMetadata();
		metadata.setTraceId(traceId);
		metadata.setBackendVersion(backendVersion);
		metadata.setResponseTimeMs(responseTimeMs);
		metadata.setRetried(retried);

		return metadata;
	}

	private ExplainEffectiveContext buildEffectiveContext(
			AiExplainResult result,
			boolean supportedLanguage
	) {
		ExplainEffectiveContext effectiveContext = new ExplainEffectiveContext();
		effectiveContext.setMode(result.getMode());
		effectiveContext.setLanguage(result.getLanguage());
		effectiveContext.setSupportedLanguage(supportedLanguage);

		return effectiveContext;
	}

	private ExplainFileContext buildFileContext(
			ExplainRequest request,
			String lineRange
	) {
		ExplainFileContext fileContext = new ExplainFileContext();
		fileContext.setFileName(request.getFileName());
		fileContext.setLineRange(lineRange);
		fileContext.setProjectName(request.getProjectName());
		fileContext.setModuleName(request.getModuleName());

		return fileContext;
	}

	private ExplainRequestContext buildRequestContext(ExplainRequest request) {
		ExplainRequestContext requestContext = new ExplainRequestContext();
		requestContext.setRequestLanguage(request.getLanguage());
		requestContext.setRequestMode(request.getMode());
		requestContext.setRequestFileName(request.getFileName());
		requestContext.setRequestProjectName(request.getProjectName());
		requestContext.setRequestModuleName(request.getModuleName());
		requestContext.setRequestPluginVersion(request.getPluginVersion());
		requestContext.setRequestIdeVersion(request.getIdeVersion());

		return requestContext;
	}
}