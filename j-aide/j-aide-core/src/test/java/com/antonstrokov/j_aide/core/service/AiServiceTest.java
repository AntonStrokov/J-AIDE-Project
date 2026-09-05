package com.antonstrokov.j_aide.core.service;

import com.antonstrokov.j_aide.core.config.AiProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiServiceTest {

	@Test
	void shouldIncludeStructuralContextInTestGenerationPrompt() {
		OllamaChatModel model = mock(OllamaChatModel.class);

		AiProperties aiProperties = new AiProperties(
				null,
				new AiProperties.Limits(10_000, 10_000)
		);

		ObjectMapper objectMapper = new ObjectMapper();

		when(model.chat(anyString())).thenReturn("""
				{
				  "summary": "Generated tests",
				  "testCode": "class StructuralContextSmokeTest {}",
				  "testFramework": "JUnit 5",
				  "coveredScenarios": ["addition"],
				  "riskHint": "none",
				  "confidence": "high"
				}
				""");

		AiService service = new AiService(
				model,
				aiProperties,
				objectMapper
		);

		String structuralContext = """
				package smoke
				class StructuralContextSmoke
				int add(int a, int b)
				""";

		service.generateTests(
				"public int add(int a, int b) { return a + b; }",
				structuralContext,
				"FAST",
				"java",
				"StructuralContextSmoke.java",
				5,
				7,
				"demo-project",
				"demo-module",
				"0.1.1",
				"2025.1"
		);

		ArgumentCaptor<String> promptCaptor =
				ArgumentCaptor.forClass(String.class);

		verify(model).chat(promptCaptor.capture());

		String prompt = promptCaptor.getValue();

		assertTrue(prompt.contains(structuralContext));
	}
}
