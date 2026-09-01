package com.antonstrokov.jaide.plugin.factory.tests;

import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.dto.tests.JaideTestGenerationRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JaideTestGenerationRequestFactoryTest {

	private final JaideTestGenerationRequestFactory factory =
			new JaideTestGenerationRequestFactory();

	@Test
	void shouldUseProvidedStructuralContext() {
		JaideEditorContext context = new JaideEditorContext(
				"fun add(a: Int, b: Int): Int = a + b",
				"Calculator.kt",
				2,
				2,
				20,
				57,
				"demo-project",
				"demo-module",
				"2026.2",
				null
		);

		String structuralContext = """
                package com.example
                class Calculator
                """;

		JaideTestGenerationRequest request =
				factory.create(context, structuralContext);

		assertEquals(structuralContext, request.structuralContext());
		assertEquals("", request.surroundingContext());
	}
}
