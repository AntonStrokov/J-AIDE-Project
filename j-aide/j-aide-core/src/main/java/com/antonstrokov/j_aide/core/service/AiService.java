package com.antonstrokov.j_aide.core.service;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@Service
public class AiService {

	private static final Logger log = LoggerFactory.getLogger(AiService.class);

	@Value("${j-aide.ai.ollama.model}")
	private String modelName;

	@Value("${j-aide.ai.ollama.base-url}")
	private String baseUrl;

	private OllamaChatModel buildModel() {
		return OllamaChatModel.builder()
				.baseUrl(baseUrl)
				.modelName(modelName)
				.timeout(Duration.ofSeconds(30))
				.build();
	}

	public String explain(String code) {

		log.info("Processing explain request, code length={}", code.length());

		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException("Code is empty");
		}

		if (code.length() > 2000) {
			throw new IllegalArgumentException("Code is too long");
		}

		OllamaChatModel model = buildModel();

		String prompt = "Explain this Java code in simple terms:\n\n" + code;

		return model.chat(prompt);
	}

	public String testLangChain() {
		Prompt prompt = Prompt.from("Explain what Java class is in one sentence");
		return prompt.text();
	}
}