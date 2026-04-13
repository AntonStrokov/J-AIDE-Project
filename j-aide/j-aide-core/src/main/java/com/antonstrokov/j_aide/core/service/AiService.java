package com.antonstrokov.j_aide.core.service;

import dev.langchain4j.model.ollama.OllamaChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AiService {

	private static final Logger log = LoggerFactory.getLogger(AiService.class);

	private final OllamaChatModel model;

	public AiService(OllamaChatModel model) {
		this.model = model;
	}

	public String explain(String code) {

		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException("Code is empty");
		}

		if (code.length() > 2000) {
			throw new IllegalArgumentException("Code is too long");
		}

		log.info("Processing explain request, code length={}", code.length());

		String prompt = "Explain this Java code in simple terms:\n\n" + code;

		return model.chat(prompt);
	}

}