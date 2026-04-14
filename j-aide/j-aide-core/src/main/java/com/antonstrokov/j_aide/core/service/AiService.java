package com.antonstrokov.j_aide.core.service;

import com.antonstrokov.j_aide.core.config.AiProperties;
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
					"Объясни код понятно и кратко (3-5 предложений).\n\n" +
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

	public AiService(OllamaChatModel model, AiProperties aiProperties) {
		this.model = model;
		this.aiProperties = aiProperties;
	}

	public String explain(String code, String mode) {

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

		return model.chat(prompt);
	}
}