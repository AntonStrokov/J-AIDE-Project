package com.antonstrokov.j_aide.core.service;

import com.antonstrokov.j_aide.core.config.AiProperties;
import com.antonstrokov.j_aide.core.dto.AiExplainResult;
import com.antonstrokov.j_aide.core.dto.StructuredExplainResponse;
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
			"Ты опытный Java-разработчик.\n" +
					"Объясни код очень кратко (1-2 предложения).\n" +
					"Только суть.\n\n" +
					"Код:\n{{code}}"
	);
	private static final PromptTemplate SMART_TEMPLATE = PromptTemplate.from(
			"Ты опытный Java-разработчик.\n" +
					"Объясни код.\n\n" +
					"Верни ответ строго в JSON формате БЕЗ markdown и БЕЗ ```.\n" +
					"Только чистый JSON.\n\n" +
					"Формат:\n" +
					"{\n" +
					"  \"summary\": \"краткое объяснение\",\n" +
					"  \"details\": \"подробное объяснение\",\n" +
					"  \"complexity\": \"easy/medium/hard\"\n" +
					"}\n\n" +
					"Не добавляй никаких пояснений.\n\n" +
					"Код:\n{{code}}"
	);
	private static final PromptTemplate DEEP_TEMPLATE = PromptTemplate.from(
			"Ты опытный Java-разработчик.\n" +
					"Подробно объясни код.\n" +
					"Разбей ответ на пункты.\n" +
					"Объясняй только данный код.\n" +
					"Не добавляй лишние примеры.\n\n" +
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

	public AiExplainResult explain(String code, String mode) {

		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException("Code is empty");
		}

		int maxCodeLength = aiProperties.limits().codeMaxLength();
		log.info("Configured maxCodeLength={}", maxCodeLength);

		if (code.length() > maxCodeLength) {
			throw new IllegalArgumentException("Code is too long");
		}


		String effectiveMode = (mode == null || mode.isBlank()) ? "SMART" : mode.toUpperCase();

		PromptTemplate template;

		switch (effectiveMode) {
			case "FAST":
				template = FAST_TEMPLATE;
				break;
			case "DEEP":
				template = DEEP_TEMPLATE;
				break;
			default:
				template = SMART_TEMPLATE;
		}

		String prompt = template.apply(Map.of("code", code)).text();

		String response = model.chat(prompt);

		response = response.trim();

		int start = response.indexOf('{');
		int end = response.lastIndexOf('}');

		if (start != -1 && end != -1 && start < end) {
			response = response.substring(start, end + 1);
		}

		try {
			StructuredExplainResponse structured =
					objectMapper.readValue(response, StructuredExplainResponse.class);

			if (structured.getSummary() == null || structured.getDetails() == null) {
				throw new RuntimeException("Invalid AI JSON structure");
			}

			return new AiExplainResult(structured, null);

		} catch (Exception e) {
			log.warn("Failed to parse AI response, returning fallback", e);

			StructuredExplainResponse fallback = new StructuredExplainResponse();
			fallback.setSummary("Не удалось структурировать ответ AI");
			fallback.setDetails(response);
			fallback.setComplexity("unknown");

			return new AiExplainResult(fallback, response);
		}
	}
}