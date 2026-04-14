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

	private static final PromptTemplate EXPLAIN_TEMPLATE = PromptTemplate.from(
			"Ты опытный Java-разработчик.\n" +
					"Объясни следующий код простым и понятным языком на русском.\n\n" +
					"ВАЖНО:\n" +
					"- Отвечай кратко (не более 5-7 предложений)\n" +
					"- Не добавляй лишние примеры\n" +
					"- Только объяснение\n\n" +
					"Код:\n{{code}}"
	);

	private final OllamaChatModel model;
	private final AiProperties aiProperties;

	public AiService(OllamaChatModel model, AiProperties aiProperties) {
		this.model = model;
		this.aiProperties = aiProperties;
	}

	public String explain(String code) {

		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException("Code is empty");
		}

		int maxCodeLength = aiProperties.limits().codeMaxLength();
		log.info("Configured maxCodeLength={}", maxCodeLength);

		if (code.length() > maxCodeLength) {
			throw new IllegalArgumentException("Code is too long");
		}

		log.info("Processing explain request, code length={}", code.length());

		String prompt = EXPLAIN_TEMPLATE.apply(Map.of("code", code)).text();

		return model.chat(prompt);
	}
}