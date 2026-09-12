package com.antonstrokov.jaide.plugin.factory.error;

import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JaideBackendErrorExplainRequestFactoryTest {

	private final JaideBackendErrorExplainRequestFactory factory =
			new JaideBackendErrorExplainRequestFactory();

	@Test
	void shouldResolveLanguageFromFileName() {
		JaideErrorExplainRequest request = new JaideErrorExplainRequest(
				"java.lang.IllegalStateException",
				"runtime_error",
				null,
				"Example.kt",
				10,
				12,
				"demo-project",
				null,
				"2025.1",
				"0.1.2",
				"demo-module"
		);

		JaideErrorExplainRequest backendRequest = factory.create(request);

		assertEquals("kotlin", backendRequest.language());
	}

	@Test
	void shouldUsePlainTextWhenFileNameIsMissing() {
		JaideErrorExplainRequest request = new JaideErrorExplainRequest(
				"Connection refused",
				"runtime_error",
				null,
				null,
				null,
				null,
				"demo-project",
				null,
				"2025.1",
				"0.1.2",
				null
		);

		JaideErrorExplainRequest backendRequest = factory.create(request);

		assertEquals("plain_text", backendRequest.language());
	}
}
