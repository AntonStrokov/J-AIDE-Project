package com.antonstrokov.jaide.plugin.factory.tests;

import com.antonstrokov.jaide.plugin.dto.tests.JaideBackendTestGenerationRequest;
import com.antonstrokov.jaide.plugin.dto.tests.JaideTestGenerationRequest;
import com.antonstrokov.jaide.plugin.model.JaideExplainMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JaideBackendTestGenerationRequestFactoryTest {

	private final JaideBackendTestGenerationRequestFactory factory =
			new JaideBackendTestGenerationRequestFactory();

	@Test
	void shouldResolveKotlinLanguageFromFileName() {
		JaideTestGenerationRequest request = new JaideTestGenerationRequest(
				"fun calculate(): Int = 42",
				JaideExplainMode.SMART,
				"Example.kt",
				1,
				1,
				"demo-project",
				"demo-module",
				"2026.2"
		);

		JaideBackendTestGenerationRequest backendRequest = factory.create(request);

		assertEquals("kotlin", backendRequest.language());
	}

	@Test
	void shouldKeepJavaLanguageFromFileName() {
		JaideTestGenerationRequest request = new JaideTestGenerationRequest(
				"class Example {}",
				JaideExplainMode.SMART,
				"Example.java",
				1,
				1,
				"demo-project",
				"demo-module",
				"2026.2"
		);

		JaideBackendTestGenerationRequest backendRequest = factory.create(request);

		assertEquals("java", backendRequest.language());
	}
}
