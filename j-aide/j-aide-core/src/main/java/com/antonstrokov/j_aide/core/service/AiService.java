package com.antonstrokov.j_aide.core.service;

import com.antonstrokov.j_aide.core.config.AiProperties;
import com.antonstrokov.j_aide.core.dto.AiExplainResult;
import com.antonstrokov.j_aide.core.dto.StructuredExplainResponse;
import com.antonstrokov.j_aide.core.dto.SupportedLanguage;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
		validateRequiredAiField(structured.getSuggestion(), "suggestion");
		validateRequiredAiField(structured.getBestPractice(), "bestPractice");
		validateRequiredAiField(structured.getRiskHint(), "riskHint");
		validateRequiredAiField(structured.getConfidence(), "confidence");
		validateRequiredAiField(structured.getCodeSmell(), "codeSmell");
		validateRequiredAiField(structured.getInputType(), "inputType");
	}

	private StructuredExplainResponse parseStructuredResponse(String response) throws Exception {
		StructuredExplainResponse structured =
				objectMapper.readValue(response, StructuredExplainResponse.class);

		validateStructuredResponse(structured);

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

	private AiExplainResult tryParseResponse(String response, String effectiveMode, String effectiveLanguage)
			throws Exception {
		StructuredExplainResponse structured = parseStructuredResponse(response);
		return new AiExplainResult(structured, null, effectiveMode, effectiveLanguage, false);
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

		validateExplainInput(
				code,
				fileName,
				lineStart,
				lineEnd,
				projectName,
				moduleName,
				pluginVersion,
				ideVersion
		);

		String effectiveMode = (mode == null || mode.isBlank()) ? "SMART" : mode.toUpperCase();

		SupportedLanguage resolvedLanguage = resolveLanguage(language);
		String effectiveLanguage = resolvedLanguage.name().toLowerCase();

		PromptTemplate template = AiPromptTemplates.resolveTemplate(effectiveMode);

		String prompt = template.apply(Map.of(
				"code", code,
				"language", effectiveLanguage,
				"fileName", fileName,
				"lineStart", lineStart,
				"lineEnd", lineEnd,
				"projectName", projectName,
				"moduleName", moduleName
		)).text();

		String response = askModel(prompt);

		boolean shouldRetry = "SMART".equals(effectiveMode);

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

				return buildFallbackResult(retryResponse != null ? retryResponse : response, effectiveMode,
						effectiveLanguage);
			}
		}
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

	private void validateExplainInput(
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
}