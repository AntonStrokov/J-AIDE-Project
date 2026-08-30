package com.antonstrokov.j_aide.core.service;

import dev.langchain4j.model.input.PromptTemplate;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiPromptTemplatesTest {

	@Test
	void shouldNotForceJavaSpecificTestInstructionsForKotlin() {
		PromptTemplate template =
				AiPromptTemplates.resolveTestGenerationTemplate("kotlin");

		String prompt = template.apply(Map.of(
				"code", "fun calculate(): Int = 42",
				"language", "kotlin",
				"fileName", "Example.kt",
				"lineStart", "1",
				"lineEnd", "1",
				"projectName", "demo-project",
				"moduleName", "demo-module"
		)).text();

		assertTrue(prompt.contains("kotlin"));
		assertFalse(prompt.contains("Java test engineer"));
		assertFalse(prompt.contains("JUnit 5 / Mockito"));
		assertFalse(prompt.contains("обычный Java-код"));
	}

	@Test
	void shouldKeepJavaSpecificTestInstructionsForJava() {
		PromptTemplate template =
				AiPromptTemplates.resolveTestGenerationTemplate("java");

		String prompt = template.apply(Map.of(
				"code", "class Example {}",
				"language", "java",
				"fileName", "Example.java",
				"lineStart", "1",
				"lineEnd", "1",
				"projectName", "demo-project",
				"moduleName", "demo-module"
		)).text();

		assertTrue(prompt.contains("Java test engineer"));
		assertTrue(prompt.contains("JUnit 5 / Mockito"));
		assertTrue(prompt.contains("обычный Java-код"));
	}
}
