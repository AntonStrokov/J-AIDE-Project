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
				"",
				"",
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
				"",
				"",
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

	@Test
	void shouldPassSurroundingContextToBackendRequest() {
		String surroundingContext = """
				class Calculator {
				    <SELECTED_CODE>
				}
				""";

		JaideTestGenerationRequest request = new JaideTestGenerationRequest(
				"fun add(a: Int, b: Int): Int = a + b",
				"",
				surroundingContext,
				JaideExplainMode.SMART,
				"Calculator.kt",
				1,
				1,
				"demo-project",
				"demo-module",
				"2026.2"
		);

		JaideBackendTestGenerationRequest backendRequest = factory.create(request);

		assertEquals(surroundingContext, backendRequest.surroundingContext());
	}

	@Test
	void shouldPassStructuralContextToBackendRequest() {
		String structuralContext = """
				package com.example
				import com.example.api.CustomPayload
				class Calculator
				""";

		JaideTestGenerationRequest request = new JaideTestGenerationRequest(
				"fun add(a: Int, b: Int): Int = a + b",
				structuralContext,
				"",
				JaideExplainMode.SMART,
				"Calculator.kt",
				1,
				1,
				"demo-project",
				"demo-module",
				"2026.2"
		);

		JaideBackendTestGenerationRequest backendRequest = factory.create(request);

		assertEquals(structuralContext, backendRequest.structuralContext());
	}
}
