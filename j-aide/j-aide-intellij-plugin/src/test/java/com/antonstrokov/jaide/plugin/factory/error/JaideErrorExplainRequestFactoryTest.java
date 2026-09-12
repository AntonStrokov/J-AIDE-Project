package com.antonstrokov.jaide.plugin.factory.error;

import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainRequest;
import com.antonstrokov.jaide.plugin.model.JaideRuntimeErrorInput;
import com.antonstrokov.jaide.plugin.model.JaideRuntimeErrorInputSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JaideErrorExplainRequestFactoryTest {

	private final JaideErrorExplainRequestFactory factory =
			new JaideErrorExplainRequestFactory();

	@Test
	void shouldPreserveSourceMetadataForEditorSelection() {
		JaideRuntimeErrorInput input = new JaideRuntimeErrorInput(
				"java.lang.NullPointerException",
				"Example.java",
				10,
				12,
				"demo-project",
				"2025.1",
				"demo-module",
				JaideRuntimeErrorInputSource.EDITOR_SELECTION
		);

		JaideErrorExplainRequest request = factory.create(input);

		assertEquals("Example.java", request.fileName());
		assertEquals(10, request.lineStart());
		assertEquals(12, request.lineEnd());
		assertEquals("demo-module", request.moduleName());
	}

	@Test
	void shouldOmitSourceMetadataForConsoleSelection() {
		JaideRuntimeErrorInput input = new JaideRuntimeErrorInput(
				"java.lang.NullPointerException",
				null,
				1,
				2,
				"demo-project",
				"2025.1",
				null,
				JaideRuntimeErrorInputSource.CONSOLE_SELECTION
		);

		JaideErrorExplainRequest request = factory.create(input);

		assertNull(request.fileName());
		assertNull(request.lineStart());
		assertNull(request.lineEnd());
		assertNull(request.moduleName());
		assertEquals("demo-project", request.projectName());
	}
}
