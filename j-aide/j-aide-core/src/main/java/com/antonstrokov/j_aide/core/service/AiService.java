package com.antonstrokov.j_aide.core.service;

import com.antonstrokov.j_aide.core.config.AiProperties;
import com.antonstrokov.j_aide.core.dto.common.SupportedLanguage;
import com.antonstrokov.j_aide.core.dto.error.AiErrorExplainResult;
import com.antonstrokov.j_aide.core.dto.error.StructuredErrorExplanationResponse;
import com.antonstrokov.j_aide.core.dto.explain.AiExplainResult;
import com.antonstrokov.j_aide.core.dto.explain.StructuredExplainResponse;
import com.antonstrokov.j_aide.core.dto.improve.AiImproveResult;
import com.antonstrokov.j_aide.core.dto.improve.StructuredImproveResponse;
import com.antonstrokov.j_aide.core.dto.tests.AiTestGenerationResult;
import com.antonstrokov.j_aide.core.dto.tests.StructuredTestGenerationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

	private static final Logger log = LoggerFactory.getLogger(AiService.class);
	private final OllamaChatModel model;
	private final AiProperties aiProperties;
	private final ObjectMapper objectMapper;

	public AiService(OllamaChatModel model, AiProperties aiProperties, ObjectMapper objectMapper) {
		this.model = model;
		this.aiProperties = aiProperties;
		this.objectMapper = objectMapper;
	}

	private void validateStructuredResponse(StructuredExplainResponse structured) {
		validateRequiredAiField(structured.getSummary(), "summary");
		validateRequiredAiField(structured.getDetails(), "details");
		validateRequiredAiField(structured.getComplexity(), "complexity");
		validateRequiredAiField(structured.getInputType(), "inputType");
	}

	private void validateStructuredImproveResponse(StructuredImproveResponse structured) {
		validateRequiredAiField(structured.getSummary(), "summary");
		validateRequiredAiField(structured.getImprovedCode(), "improvedCode");
		validateRequiredAiField(structured.getRiskHint(), "riskHint");
		validateRequiredAiField(structured.getConfidence(), "confidence");

		if (structured.getChanges() == null) {
			throw new RuntimeException("Invalid AI JSON structure: changes is missing");
		}
	}

	private void validateStructuredTestGenerationResponse(StructuredTestGenerationResponse structured) {
		validateRequiredAiField(structured.getSummary(), "summary");
		validateRequiredAiField(structured.getTestCode(), "testCode");
		validateRequiredAiField(structured.getTestFramework(), "testFramework");
		validateRequiredAiField(structured.getConfidence(), "confidence");

		if (structured.getRiskHint() == null || structured.getRiskHint().isBlank()) {
			structured.setRiskHint("Явных рисков не обнаружено");
		}

		if (structured.getCoveredScenarios() == null) {
			throw new RuntimeException("Invalid AI JSON structure: coveredScenarios is missing");
		}
	}

	private void validateStructuredErrorExplanationResponse(StructuredErrorExplanationResponse structured) {
		validateRequiredAiField(structured.getSummary(), "summary");
		validateRequiredAiField(structured.getLikelyCause(), "likelyCause");
		validateRequiredAiField(structured.getWhereToLook(), "whereToLook");
		validateRequiredAiField(structured.getRiskHint(), "riskHint");
		validateRequiredAiField(structured.getConfidence(), "confidence");

		if (structured.getSuggestedFixes() == null) {
			throw new RuntimeException("Invalid AI JSON structure: suggestedFixes is missing");
		}
	}

	private StructuredExplainResponse parseStructuredResponse(String response) throws Exception {
		StructuredExplainResponse structured =
				objectMapper.readValue(response, StructuredExplainResponse.class);

		validateStructuredResponse(structured);

		return structured;
	}

	private StructuredImproveResponse parseStructuredImproveResponse(String response) throws Exception {
		StructuredImproveResponse structured =
				objectMapper.readValue(response, StructuredImproveResponse.class);

		validateStructuredImproveResponse(structured);

		return structured;
	}

	private StructuredTestGenerationResponse parseStructuredTestGenerationResponse(String response) throws Exception {
		StructuredTestGenerationResponse structured =
				objectMapper.readValue(response, StructuredTestGenerationResponse.class);

		validateStructuredTestGenerationResponse(structured);

		return structured;
	}

	private StructuredErrorExplanationResponse parseStructuredErrorExplanationResponse(String response)
			throws Exception {
		StructuredErrorExplanationResponse structured =
				objectMapper.readValue(response, StructuredErrorExplanationResponse.class);

		validateStructuredErrorExplanationResponse(structured);

		return structured;
	}

	private String normalizeJsonCandidate(String response) {
		response = response.replace("```json", "")
				.replace("```", "")
				.trim();

		int start = response.indexOf("{\"summary\"");
		int end = response.lastIndexOf('}');

		if (start != -1 && end != -1 && start < end) {
			response = response.substring(start, end + 1);
		}

		return response;
	}

	private String buildRetryPrompt(String prompt) {
		return prompt + "\n\n" +
				"Твой предыдущий ответ был невалидным.\n" +
				"Исправь его и верни только корректный JSON строго по указанной структуре.\n" +
				"Без markdown, без ``` и без пояснений.";
	}

	private AiExplainResult buildFallbackResult(String rawResponse, String effectiveMode, String effectiveLanguage) {
		StructuredExplainResponse fallback = new StructuredExplainResponse();
		fallback.setSummary("Не удалось структурировать ответ AI");
		fallback.setDetails(rawResponse);
		fallback.setComplexity("unknown");
		fallback.setSuggestion("Проверь сырой ответ модели в rawJson");

		return new AiExplainResult(fallback, rawResponse, effectiveMode, effectiveLanguage, false);
	}

	private AiImproveResult buildImproveFallbackResult(String rawResponse, String effectiveMode,
	                                                   String effectiveLanguage) {
		StructuredImproveResponse fallback = new StructuredImproveResponse();
		fallback.setSummary("Не удалось структурировать ответ AI");
		fallback.setImprovedCode("");
		fallback.setChanges(List.of("Проверь сырой ответ модели в rawJson"));
		fallback.setRiskHint("Улучшенный код не был применён, потому что ответ AI не удалось распарсить");
		fallback.setConfidence("low");

		return new AiImproveResult(fallback, rawResponse, effectiveMode, effectiveLanguage, false);
	}

	private AiTestGenerationResult buildTestGenerationFallbackResult(
			String rawResponse,
			String effectiveMode,
			String effectiveLanguage
	) {
		StructuredTestGenerationResponse fallback = new StructuredTestGenerationResponse();
		fallback.setSummary("Не удалось структурировать ответ AI");
		fallback.setTestCode("");
		fallback.setTestFramework("unknown");
		fallback.setCoveredScenarios(List.of("Проверь сырой ответ модели в rawJson"));
		fallback.setRiskHint("Тестовый код не был сгенерирован надёжно, потому что ответ AI не удалось распарсить");
		fallback.setConfidence("low");

		return new AiTestGenerationResult(fallback, rawResponse, effectiveMode, effectiveLanguage, false);
	}

	private AiErrorExplainResult buildErrorExplainFallbackResult(String rawResponse, String effectiveMode,
	                                                             String effectiveLanguage) {
		StructuredErrorExplanationResponse fallback = new StructuredErrorExplanationResponse();
		fallback.setSummary("Не удалось структурировать ответ AI");
		fallback.setLikelyCause("Ответ AI не удалось распарсить в ожидаемую JSON-структуру");
		fallback.setWhereToLook("Проверь сырой ответ модели в rawJson");
		fallback.setSuggestedFixes(List.of("Повтори запрос или уточни текст ошибки"));
		fallback.setRiskHint("Ошибка не была проанализирована надёжно, потому что ответ AI не удалось " +
				"структурировать");
		fallback.setConfidence("low");

		return new AiErrorExplainResult(fallback, rawResponse, effectiveMode, effectiveLanguage, false);
	}

	private AiExplainResult tryParseResponse(String response, String effectiveMode, String effectiveLanguage)
			throws Exception {
		StructuredExplainResponse structured = parseStructuredResponse(response);
		return new AiExplainResult(structured, null, effectiveMode, effectiveLanguage, false);
	}

	private AiImproveResult tryParseImproveResponse(String response, String effectiveMode, String effectiveLanguage)
			throws Exception {
		StructuredImproveResponse structured = parseStructuredImproveResponse(response);
		return new AiImproveResult(structured, null, effectiveMode, effectiveLanguage, false);
	}

	private AiTestGenerationResult tryParseTestGenerationResponse(
			String response,
			String effectiveMode,
			String effectiveLanguage
	) throws Exception {
		StructuredTestGenerationResponse structured = parseStructuredTestGenerationResponse(response);
		return new AiTestGenerationResult(structured, null, effectiveMode, effectiveLanguage, false);
	}

	private AiErrorExplainResult tryParseErrorExplanationResponse(String response, String effectiveMode,
	                                                              String effectiveLanguage) throws Exception {
		StructuredErrorExplanationResponse structured = parseStructuredErrorExplanationResponse(response);
		return new AiErrorExplainResult(structured, null, effectiveMode, effectiveLanguage, false);
	}

	public AiExplainResult explain(
			String code,
			String mode,
			String language,
			String fileName,
			Integer lineStart,
			Integer lineEnd,
			String projectName,
			String moduleName,
			String pluginVersion,
			String ideVersion) {

		validateAiRequestInput(
				code,
				fileName,
				lineStart,
				lineEnd,
				projectName,
				moduleName,
				pluginVersion,
				ideVersion
		);

		String effectiveMode = resolveMode(mode);

		SupportedLanguage resolvedLanguage = resolveLanguage(language);
		String effectiveLanguage = resolvedLanguage.name().toLowerCase();

		String prompt = buildPrompt(
				effectiveMode,
				code,
				effectiveLanguage,
				fileName,
				lineStart,
				lineEnd,
				projectName,
				moduleName
		);

		String response = askModel(prompt);

		boolean shouldRetry = shouldRetry(effectiveMode);

		return parseWithRetry(
				response,
				prompt,
				shouldRetry,
				effectiveMode,
				effectiveLanguage
		);
	}

	public AiImproveResult improve(
			String code,
			String mode,
			String language,
			String fileName,
			Integer lineStart,
			Integer lineEnd,
			String projectName,
			String moduleName,
			String pluginVersion,
			String ideVersion
	) {
		validateAiRequestInput(
				code,
				fileName,
				lineStart,
				lineEnd,
				projectName,
				moduleName,
				pluginVersion,
				ideVersion
		);

		String effectiveMode = resolveMode(mode);

		SupportedLanguage resolvedLanguage = resolveLanguage(language);
		String effectiveLanguage = resolvedLanguage.name().toLowerCase();

		String prompt = buildImprovePrompt(
				code,
				effectiveLanguage,
				fileName,
				lineStart,
				lineEnd,
				projectName,
				moduleName
		);

		String response = askModel(prompt);

		boolean shouldRetry = shouldRetry(effectiveMode);

		return parseImproveWithRetry(
				response,
				prompt,
				shouldRetry,
				effectiveMode,
				effectiveLanguage
		);
	}

	public AiTestGenerationResult generateTests(
			String code,
			String mode,
			String language,
			String fileName,
			Integer lineStart,
			Integer lineEnd,
			String projectName,
			String moduleName,
			String pluginVersion,
			String ideVersion
	) {
		validateAiRequestInput(
				code,
				fileName,
				lineStart,
				lineEnd,
				projectName,
				moduleName,
				pluginVersion,
				ideVersion
		);

		String effectiveMode = resolveMode(mode);

		SupportedLanguage resolvedLanguage = resolveLanguage(language);
		String effectiveLanguage = resolvedLanguage.name().toLowerCase();

		String prompt = buildTestGenerationPrompt(
				code,
				effectiveLanguage,
				fileName,
				lineStart,
				lineEnd,
				projectName,
				moduleName
		);

		String response = askModel(prompt);

		boolean shouldRetry = shouldRetry(effectiveMode);

		return parseTestGenerationWithRetry(
				response,
				prompt,
				shouldRetry,
				effectiveMode,
				effectiveLanguage
		);
	}

	public AiErrorExplainResult explainError(
			String errorText,
			String mode,
			String language,
			String fileName,
			Integer lineStart,
			Integer lineEnd,
			String projectName,
			String moduleName,
			String pluginVersion,
			String ideVersion
	) {
		validateErrorText(errorText);
		validateLineRange(lineStart, lineEnd);
		validateOptionalTextField(fileName, "fileName");
		validateOptionalTextField(projectName, "projectName");
		validateOptionalTextField(moduleName, "moduleName");
		validateOptionalTextField(pluginVersion, "pluginVersion");
		validateOptionalTextField(ideVersion, "ideVersion");

		String effectiveMode = resolveMode(mode);

		SupportedLanguage resolvedLanguage = resolveLanguage(language);
		String effectiveLanguage = resolvedLanguage.name().toLowerCase();

		String prompt = buildErrorExplainPrompt(
				errorText,
				effectiveLanguage,
				fileName,
				lineStart,
				lineEnd,
				projectName,
				moduleName
		);

		String response = askModel(prompt);

		boolean shouldRetry = shouldRetry(effectiveMode);

		return parseErrorExplainWithRetry(
				response,
				prompt,
				shouldRetry,
				effectiveMode,
				effectiveLanguage
		);
	}

	private SupportedLanguage resolveLanguage(String language) {
		if (language == null || language.isBlank()) {
			return SupportedLanguage.JAVA;
		}

		switch (language.toLowerCase()) {
			case "java":
				return SupportedLanguage.JAVA;
			case "kotlin":
				return SupportedLanguage.KOTLIN;
			case "sql":
				return SupportedLanguage.SQL;
			case "xml":
				return SupportedLanguage.XML;
			case "javascript":
			case "js":
				return SupportedLanguage.JAVASCRIPT;
			default:
				return SupportedLanguage.PLAIN_TEXT;
		}
	}

	private void validateOptionalTextField(String value, String fieldName) {
		if (value != null && value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " must not be blank");
		}
	}

	private void validateLineRange(Integer lineStart, Integer lineEnd) {
		if (lineStart != null && lineStart < 1) {
			throw new IllegalArgumentException("lineStart must be >= 1");
		}

		if (lineEnd != null && lineEnd < 1) {
			throw new IllegalArgumentException("lineEnd must be >= 1");
		}

		if (lineStart != null && lineEnd != null && lineStart > lineEnd) {
			throw new IllegalArgumentException("lineStart must be <= lineEnd");
		}
	}

	private void validateCode(String code) {
		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException("Code is empty");
		}

		int maxCodeLength = aiProperties.limits().codeMaxLength();

		log.info("Configured maxCodeLength={}", maxCodeLength);

		if (code.length() > maxCodeLength) {
			throw new IllegalArgumentException("Code is too long");
		}
	}

	private void validateErrorText(String errorText) {
		if (errorText == null || errorText.isBlank()) {
			throw new IllegalArgumentException("Error text is empty");
		}

		int maxErrorLength = aiProperties.limits().errorMaxLength();

		log.info("Configured maxErrorTextLength={}", maxErrorLength);

		if (errorText.length() > maxErrorLength) {
			throw new IllegalArgumentException("Error text is too long");
		}
	}

	private void validateAiRequestInput(
			String code,
			String fileName,
			Integer lineStart,
			Integer lineEnd,
			String projectName,
			String moduleName,
			String pluginVersion,
			String ideVersion
	) {
		validateCode(code);
		validateLineRange(lineStart, lineEnd);
		validateOptionalTextField(fileName, "fileName");
		validateOptionalTextField(projectName, "projectName");
		validateOptionalTextField(moduleName, "moduleName");
		validateOptionalTextField(pluginVersion, "pluginVersion");
		validateOptionalTextField(ideVersion, "ideVersion");
	}

	private void validateRequiredAiField(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new RuntimeException("Invalid AI JSON structure: " + fieldName + " is missing");
		}
	}

	private String askModel(String prompt) {
		String response = model.chat(prompt);
		return normalizeJsonCandidate(response);
	}

	private String buildPrompt(
			String effectiveMode,
			String code,
			String effectiveLanguage,
			String fileName,
			Integer lineStart,
			Integer lineEnd,
			String projectName,
			String moduleName
	) {
		PromptTemplate template = AiPromptTemplates.resolveTemplate(effectiveMode);

		return template.apply(Map.of(
				"code", code,
				"language", effectiveLanguage,
				"fileName", safeText(fileName),
				"lineStart", safeNumber(lineStart),
				"lineEnd", safeNumber(lineEnd),
				"projectName", safeText(projectName),
				"moduleName", safeText(moduleName)
		)).text();
	}

	private String buildImprovePrompt(
			String code,
			String effectiveLanguage,
			String fileName,
			Integer lineStart,
			Integer lineEnd,
			String projectName,
			String moduleName
	) {
		PromptTemplate template = AiPromptTemplates.resolveImproveTemplate();

		return template.apply(Map.of(
				"code", code,
				"language", effectiveLanguage,
				"fileName", safeText(fileName),
				"lineStart", safeNumber(lineStart),
				"lineEnd", safeNumber(lineEnd),
				"projectName", safeText(projectName),
				"moduleName", safeText(moduleName)
		)).text();
	}

	private String buildTestGenerationPrompt(
			String code,
			String effectiveLanguage,
			String fileName,
			Integer lineStart,
			Integer lineEnd,
			String projectName,
			String moduleName
	) {
		PromptTemplate template = AiPromptTemplates.resolveTestGenerationTemplate();

		return template.apply(Map.of(
				"code", code,
				"language", effectiveLanguage,
				"fileName", safeText(fileName),
				"lineStart", safeNumber(lineStart),
				"lineEnd", safeNumber(lineEnd),
				"projectName", safeText(projectName),
				"moduleName", safeText(moduleName)
		)).text();
	}

	private String buildErrorExplainPrompt(
			String errorText,
			String effectiveLanguage,
			String fileName,
			Integer lineStart,
			Integer lineEnd,
			String projectName,
			String moduleName
	) {
		PromptTemplate template = AiPromptTemplates.resolveErrorExplainTemplate();

		return template.apply(Map.of(
				"errorText", errorText,
				"language", effectiveLanguage,
				"fileName", safeText(fileName),
				"lineStart", safeNumber(lineStart),
				"lineEnd", safeNumber(lineEnd),
				"projectName", safeText(projectName),
				"moduleName", safeText(moduleName)
		)).text();
	}

	private String safeText(String value) {
		return value == null ? "not provided" : value;
	}

	private String safeNumber(Integer value) {
		return value == null ? "not provided" : value.toString();
	}

	private AiExplainResult parseWithRetry(
			String response,
			String prompt,
			boolean shouldRetry,
			String effectiveMode,
			String effectiveLanguage
	) {
		try {
			return tryParseResponse(response, effectiveMode, effectiveLanguage);

		} catch (Exception e) {
			if (!shouldRetry) {
				log.warn("AI response parsing failed, returning fallback without retry", e);

				return buildFallbackResult(response, effectiveMode, effectiveLanguage);
			}

			log.warn("First AI response parsing failed, retrying once", e);

			String retryResponse = null;

			try {
				String retryPrompt = buildRetryPrompt(prompt);

				retryResponse = askModel(retryPrompt);

				AiExplainResult retryResult = tryParseResponse(retryResponse, effectiveMode, effectiveLanguage);
				retryResult.setRetried(true);

				log.info("Retry succeeded and returned valid structured response");

				return retryResult;

			} catch (Exception retryException) {
				log.warn("Retry also failed, returning fallback", retryException);

				return buildFallbackResult(
						retryResponse != null ? retryResponse : response,
						effectiveMode,
						effectiveLanguage
				);
			}
		}
	}

	private AiImproveResult parseImproveWithRetry(
			String response,
			String prompt,
			boolean shouldRetry,
			String effectiveMode,
			String effectiveLanguage
	) {
		try {
			return tryParseImproveResponse(response, effectiveMode, effectiveLanguage);

		} catch (Exception e) {
			if (!shouldRetry) {
				log.warn("AI improve response parsing failed, returning fallback without retry", e);

				return buildImproveFallbackResult(response, effectiveMode, effectiveLanguage);
			}

			log.warn("First AI improve response parsing failed, retrying once", e);

			String retryResponse = null;

			try {
				String retryPrompt = buildRetryPrompt(prompt);

				retryResponse = askModel(retryPrompt);

				AiImproveResult retryResult = tryParseImproveResponse(
						retryResponse,
						effectiveMode,
						effectiveLanguage
				);
				retryResult.setRetried(true);

				log.info("Retry succeeded and returned valid structured improve response");

				return retryResult;

			} catch (Exception retryException) {
				log.warn("Improve retry also failed, returning fallback", retryException);

				return buildImproveFallbackResult(
						retryResponse != null ? retryResponse : response,
						effectiveMode,
						effectiveLanguage
				);
			}
		}
	}

	private AiTestGenerationResult parseTestGenerationWithRetry(
			String response,
			String prompt,
			boolean shouldRetry,
			String effectiveMode,
			String effectiveLanguage
	) {
		try {
			return tryParseTestGenerationResponse(response, effectiveMode, effectiveLanguage);

		} catch (Exception e) {
			if (!shouldRetry) {
				log.warn("AI test generation response parsing failed, returning fallback without retry", e);

				return buildTestGenerationFallbackResult(response, effectiveMode, effectiveLanguage);
			}

			log.warn("First AI test generation response parsing failed, retrying once", e);

			String retryResponse = null;

			try {
				String retryPrompt = buildRetryPrompt(prompt);

				retryResponse = askModel(retryPrompt);

				AiTestGenerationResult retryResult = tryParseTestGenerationResponse(
						retryResponse,
						effectiveMode,
						effectiveLanguage
				);
				retryResult.setRetried(true);

				log.info("Retry succeeded and returned valid structured test generation response");

				return retryResult;

			} catch (Exception retryException) {
				log.warn("Test generation retry also failed, returning fallback", retryException);

				return buildTestGenerationFallbackResult(
						retryResponse != null ? retryResponse : response,
						effectiveMode,
						effectiveLanguage
				);
			}
		}
	}


	private AiErrorExplainResult parseErrorExplainWithRetry(
			String response,
			String prompt,
			boolean shouldRetry,
			String effectiveMode,
			String effectiveLanguage
	) {
		try {
			return tryParseErrorExplanationResponse(response, effectiveMode, effectiveLanguage);

		} catch (Exception e) {
			if (!shouldRetry) {
				log.warn("AI error explanation response parsing failed, returning fallback without retry", e);

				return buildErrorExplainFallbackResult(response, effectiveMode, effectiveLanguage);
			}

			log.warn("First AI error explanation response parsing failed, retrying once", e);

			String retryResponse = null;

			try {
				String retryPrompt = buildRetryPrompt(prompt);

				retryResponse = askModel(retryPrompt);

				AiErrorExplainResult retryResult = tryParseErrorExplanationResponse(
						retryResponse,
						effectiveMode,
						effectiveLanguage
				);
				retryResult.setRetried(true);

				log.info("Retry succeeded and returned valid structured error explanation response");

				return retryResult;

			} catch (Exception retryException) {
				log.warn("Error explanation retry also failed, returning fallback", retryException);

				return buildErrorExplainFallbackResult(
						retryResponse != null ? retryResponse : response,
						effectiveMode,
						effectiveLanguage
				);
			}
		}
	}

	private String resolveMode(String mode) {
		return (mode == null || mode.isBlank()) ? "SMART" : mode.toUpperCase();
	}

	private boolean shouldRetry(String effectiveMode) {
		return "SMART".equals(effectiveMode);
	}
}
