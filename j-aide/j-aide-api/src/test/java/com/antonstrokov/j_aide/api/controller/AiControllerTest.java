package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.api.dto.tests.TestGenerationRequest;
import com.antonstrokov.j_aide.core.config.AppProperties;
import com.antonstrokov.j_aide.core.dto.tests.AiTestGenerationResult;
import com.antonstrokov.j_aide.core.dto.tests.StructuredTestGenerationResponse;
import com.antonstrokov.j_aide.core.service.AiService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiControllerTest {

	@Test
	void shouldPassStructuralContextToAiService() {
		AiService aiService = mock(AiService.class);

		AppProperties appProperties = new AppProperties(
				"j-aide",
				"0.1.1"
		);

		AiController controller = new AiController(
				aiService,
				appProperties
		);

		String structuralContext = """
				package smoke
				class StructuralContextSmoke
				int add(int a, int b)
				""";

		TestGenerationRequest request = new TestGenerationRequest();
		request.setCode("public int add(int a, int b) { return a + b; }");
		request.setStructuralContext(structuralContext);
		request.setSurroundingContext("");
		request.setMode("FAST");
		request.setLanguage("java");
		request.setFileName("StructuralContextSmoke.java");
		request.setLineStart(5);
		request.setLineEnd(7);
		request.setProjectName("demo-project");
		request.setModuleName("demo-module");
		request.setPluginVersion("0.1.1");
		request.setIdeVersion("2025.1");

		StructuredTestGenerationResponse structuredResponse =
				new StructuredTestGenerationResponse();

		structuredResponse.setSummary("Generated tests");
		structuredResponse.setTestCode("class StructuralContextSmokeTest {}");
		structuredResponse.setTestFramework("JUnit 5");
		structuredResponse.setCoveredScenarios(List.of("addition"));
		structuredResponse.setRiskHint("none");
		structuredResponse.setConfidence("high");

		AiTestGenerationResult aiResult = new AiTestGenerationResult(
				structuredResponse,
				null,
				"FAST",
				"java",
				false
		);

		when(aiService.generateTests(
				request.getCode(),
				request.getStructuralContext(),
				request.getMode(),
				request.getLanguage(),
				request.getFileName(),
				request.getLineStart(),
				request.getLineEnd(),
				request.getProjectName(),
				request.getModuleName(),
				request.getPluginVersion(),
				request.getIdeVersion()
		)).thenReturn(aiResult);

		controller.generateTests(request);

		verify(aiService).generateTests(
				request.getCode(),
				structuralContext,
				request.getMode(),
				request.getLanguage(),
				request.getFileName(),
				request.getLineStart(),
				request.getLineEnd(),
				request.getProjectName(),
				request.getModuleName(),
				request.getPluginVersion(),
				request.getIdeVersion()
		);
	}
}
