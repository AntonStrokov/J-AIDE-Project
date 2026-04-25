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
	private static final PromptTemplate FAST_TEMPLATE = PromptTemplate.from(
			"Ты опытный {{language}}-разработчик.\n" +
					"Объясни код очень кратко на русском языке.\n\n" +
					"Верни ответ строго в JSON формате БЕЗ markdown и БЕЗ ```.\n" +
					"Только чистый JSON.\n\n" +
					"- Если вход содержит синтаксис языка программирования, inputType должен быть code\n" +
					"- Если вход является обычным текстом без программного синтаксиса, inputType должен быть " +
					"plain_text\n\n" +
					"Формат:\n" +
					"{\n" +
					"  \"summary\": \"очень краткое объяснение\",\n" +
					"  \"details\": \"1-2 коротких предложения\",\n" +
					"  \"complexity\": \"easy/medium/hard\", \n" +
					"  \"suggestion\": \"короткая рекомендация\", \n" +
					"  \"bestPractice\": \"какая хорошая практика здесь уместна\", \n" +
					"  \"riskHint\": \"есть ли здесь риск или на что стоит обратить внимание\", \n" +
					"  \"confidence\": \"high/medium/low\", \n" +
					"  \"codeSmell\": \"есть ли здесь запах кода или краткая оценка качества\", \n" +
					"  \"inputType\": \"одно значение: code или plain_text\"\n" +
					"}\n\n" +
					"Не добавляй никаких пояснений.\n\n" +
					"Имя проекта: {{projectName}}\n" +
					"Имя модуля: {{moduleName}}\n" +
					"Имя файла: {{fileName}}\n" +
					"Диапазон строк: {{lineStart}}-{{lineEnd}}\n\n" +
					"Код:\n{{code}}"
	);
	private static final PromptTemplate SMART_TEMPLATE = PromptTemplate.from(
			"Ты опытный {{language}}-разработчик.\n" +
					"Объясни код на русском языке.\n\n" +
					"Верни ответ строго в JSON формате БЕЗ markdown и БЕЗ ```.\n" +
					"Только чистый JSON.\n\n" +
					"- Если вход содержит синтаксис языка программирования, inputType должен быть code\n" +
					"- Если вход является обычным текстом без программного синтаксиса, inputType должен быть " +
					"plain_text\n\n" +
					"Формат:\n" +
					"{\n" +
					"  \"summary\": \"краткое объяснение\",\n" +
					"  \"details\": \"подробное объяснение\",\n" +
					"  \"complexity\": \"easy/medium/hard\", \n" +
					"  \"suggestion\": \"что можно улучшить или на что обратить внимание\", \n" +
					"  \"bestPractice\": \"какая хорошая практика здесь уместна\", \n" +
					"  \"riskHint\": \"есть ли здесь риск или на что стоит обратить внимание\", \n" +
					"  \"confidence\": \"high/medium/low\", \n" +
					"  \"codeSmell\": \"есть ли здесь запах кода или краткая оценка качества\", \n" +
					"  \"inputType\": \"одно значение: code или plain_text\"\n" +
					"}\n\n" +
					"Не добавляй никаких пояснений.\n\n" +
					"Имя проекта: {{projectName}}\n" +
					"Имя модуля: {{moduleName}}\n" +
					"Имя файла: {{fileName}}\n\n" +
					"Диапазон строк: {{lineStart}}-{{lineEnd}}\n\n" +
					"Код:\n{{code}}"
	);
	private static final PromptTemplate DEEP_TEMPLATE = PromptTemplate.from(
			"Ты опытный {{language}}-разработчик.\n" +
					"Подробно объясни код на русском языке.\n" +
					"Объясняй только данный код.\n" +
					"Не добавляй лишние примеры.\n\n" +
					"Верни ответ строго в JSON формате БЕЗ markdown и БЕЗ ```.\n" +
					"Только чистый JSON.\n\n" +
					"ВАЖНО:\n" +
					"- Все значения в JSON должны быть строками\n" +
					"- Поле details должно быть строкой, а не объектом и не массивом\n" +
					"- Если вход содержит синтаксис языка программирования, inputType должен быть code\n" +
					"- Если вход является обычным текстом без программного синтаксиса, inputType должен быть " +
					"plain_text\n" +
					"- В details обязательно опиши: 1) что делает код, 2) ключевые элементы синтаксиса, 3) что здесь " +
					"отсутствует или упрощено, 4) где такой код может использоваться\n\n" +
					"Формат:\n" +
					"{\n" +
					"  \"summary\": \"краткий вывод\",\n" +
					"  \"details\": \"подробное объяснение в несколько предложений, можно с нумерацией внутри " +
					"строки\",\n" +
					"  \"complexity\": \"easy/medium/hard\", \n" +
					"  \"suggestion\": \"что можно улучшить или на что обратить внимание\", \n" +
					"  \"bestPractice\": \"какая хорошая практика здесь уместна\", \n" +
					"  \"riskHint\": \"есть ли здесь риск или на что стоит обратить внимание\", \n" +
					"  \"confidence\": \"high/medium/low\", \n" +
					"  \"codeSmell\": \"есть ли здесь запах кода или краткая оценка качества\", \n" +
					"  \"inputType\": \"одно значение: code или plain_text\"\n" +
					"}\n\n" +
					"Не добавляй никаких пояснений вне JSON.\n\n" +
					"Имя проекта: {{projectName}}\n" +
					"Имя модуля: {{moduleName}}\n" +
					"Имя файла: {{fileName}}\n" +
					"Диапазон строк: {{lineStart}}-{{lineEnd}}\n\n" +
					"Код:\n{{code}}"
	);
	private final OllamaChatModel model;
	private final AiProperties aiProperties;
	private final ObjectMapper objectMapper;


	public AiService(OllamaChatModel model, AiProperties aiProperties, ObjectMapper objectMapper) {
		this.model = model;
		this.aiProperties = aiProperties;
		this.objectMapper = objectMapper;
	}

	private void validateStructuredResponse(StructuredExplainResponse structured) {
		if (structured.getSummary() == null || structured.getSummary().isBlank()) {
			throw new RuntimeException("Invalid AI JSON structure: summary is missing");
		}

		if (structured.getDetails() == null || structured.getDetails().isBlank()) {
			throw new RuntimeException("Invalid AI JSON structure: details are missing");
		}

		if (structured.getSuggestion() == null || structured.getSuggestion().isBlank()) {
			throw new RuntimeException("Invalid AI JSON structure: suggestion is missing");
		}

		if (structured.getBestPractice() == null || structured.getBestPractice().isBlank()) {
			throw new RuntimeException("Invalid AI JSON structure: bestPractice is missing");
		}

		if (structured.getRiskHint() == null || structured.getRiskHint().isBlank()) {
			throw new RuntimeException("Invalid AI JSON structure: riskHint is missing");
		}

		if (structured.getConfidence() == null || structured.getConfidence().isBlank()) {
			throw new RuntimeException("Invalid AI JSON structure: confidence is missing");
		}

		if (structured.getCodeSmell() == null || structured.getCodeSmell().isBlank()) {
			throw new RuntimeException("Invalid AI JSON structure: codeSmell is missing");
		}

		if (structured.getInputType() == null || structured.getInputType().isBlank()) {
			throw new RuntimeException("Invalid AI JSON structure: inputType is missing");
		}
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

	private PromptTemplate resolveTemplate(String effectiveMode) {
		switch (effectiveMode) {
			case "FAST":
				return FAST_TEMPLATE;
			case "DEEP":
				return DEEP_TEMPLATE;
			default:
				return SMART_TEMPLATE;
		}
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

		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException("Code is empty");
		}

		if (lineStart != null && lineStart < 1) {
			throw new IllegalArgumentException("lineStart must be >= 1");
		}

		if (lineEnd != null && lineEnd < 1) {
			throw new IllegalArgumentException("lineEnd must be >= 1");
		}

		if (lineStart != null && lineEnd != null && lineStart > lineEnd) {
			throw new IllegalArgumentException("lineStart must be <= lineEnd");
		}

		if (fileName != null && fileName.isBlank()) {
			throw new IllegalArgumentException("fileName must not be blank");
		}

		if (projectName != null && projectName.isBlank()) {
			throw new IllegalArgumentException("projectName must not be blank");
		}

		if (moduleName != null && moduleName.isBlank()) {
			throw new IllegalArgumentException("moduleName must not be blank");
		}

		if (pluginVersion != null && pluginVersion.isBlank()) {
			throw new IllegalArgumentException("pluginVersion must not be blank");
		}

		if (ideVersion != null && ideVersion.isBlank()) {
			throw new IllegalArgumentException("ideVersion must not be blank");
		}

		int maxCodeLength = aiProperties.limits().codeMaxLength();

		log.info("Configured maxCodeLength={}", maxCodeLength);

		if (code.length() > maxCodeLength) {
			throw new IllegalArgumentException("Code is too long");
		}

		String effectiveMode = (mode == null || mode.isBlank()) ? "SMART" : mode.toUpperCase();

		SupportedLanguage resolvedLanguage = resolveLanguage(language);
		String effectiveLanguage = resolvedLanguage.name().toLowerCase();

		PromptTemplate template = resolveTemplate(effectiveMode);

		String prompt = template.apply(Map.of(
				"code", code,
				"language", effectiveLanguage,
				"fileName", fileName,
				"lineStart", lineStart,
				"lineEnd", lineEnd,
				"projectName", projectName,
				"moduleName", moduleName
		)).text();

		String response = model.chat(prompt);

		response = normalizeJsonCandidate(response);

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

				retryResponse = model.chat(retryPrompt);

				retryResponse = normalizeJsonCandidate(retryResponse);

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
}