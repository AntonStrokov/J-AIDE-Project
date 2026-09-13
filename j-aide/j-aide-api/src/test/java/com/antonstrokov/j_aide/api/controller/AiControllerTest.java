package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.api.dto.tests.TestGenerationRequest;
import com.antonstrokov.j_aide.core.config.AppProperties;
import com.antonstrokov.j_aide.core.dto.tests.AiTestGenerationResult;
import com.antonstrokov.j_aide.core.dto.tests.StructuredTestGenerationResponse;
import com.antonstrokov.j_aide.core.service.AiService;
import org.junit.jupiter.api.Test;
import com.antonstrokov.j_aide.api.dto.improve.ImproveRequest;
import com.antonstrokov.j_aide.api.dto.improve.ImproveResponse;
import com.antonstrokov.j_aide.core.dto.improve.AiImproveResult;
import com.antonstrokov.j_aide.core.dto.improve.StructuredImproveChangeFact;
import com.antonstrokov.j_aide.core.dto.improve.StructuredImproveResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

	@Test
	void shouldMapStructuredImproveChangeFactsToApiResponse() {
		AiService aiService = mock(AiService.class);

		AppProperties appProperties = new AppProperties(
				"j-aide",
				"0.1.2"
		);

		AiController controller = new AiController(
				aiService,
				appProperties
		);

		ImproveRequest request = new ImproveRequest();
		request.setCode("""
                        class Example {
                                void main() {
                                }
                        }
                        """);
		request.setMode("FAST");
		request.setLanguage("java");
		request.setFileName("Example.java");
		request.setLineStart(1);
		request.setLineEnd(5);
		request.setProjectName("demo-project");
		request.setModuleName("demo-module");
		request.setPluginVersion("0.1.2");
		request.setIdeVersion("2025.1");

		StructuredImproveChangeFact changeFact =
				new StructuredImproveChangeFact();
		changeFact.setOperation("rename");
		changeFact.setSymbolKind("method");
		changeFact.setBefore("main");
		changeFact.setAfter("executeProgram");

		StructuredImproveResponse structuredResponse =
				new StructuredImproveResponse();
		structuredResponse.setSummary("Improved method name");
		structuredResponse.setImprovedCode("""
                        class Example {
                                void executeProgram() {
                                }
                        }
                        """);
		structuredResponse.setChanges(
				List.of("Метод main переименован в executeProgram")
		);
		structuredResponse.setChangeFacts(List.of(changeFact));
		structuredResponse.setRiskHint("low");
		structuredResponse.setConfidence("high");

		AiImproveResult aiResult = new AiImproveResult(
				structuredResponse,
				null,
				"FAST",
				"java",
				false
		);

		when(aiService.improve(
				request.getCode(),
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

		ImproveResponse response = controller.improve(request);

		assertNotNull(response.getImprovement());
		assertNotNull(response.getImprovement().getChangeFacts());
		assertEquals(
				1,
				response.getImprovement().getChangeFacts().size()
		);
		assertEquals(
				"rename",
				response.getImprovement()
						.getChangeFacts()
						.getFirst()
						.getOperation()
		);
		assertEquals(
				"method",
				response.getImprovement()
						.getChangeFacts()
						.getFirst()
						.getSymbolKind()
		);
		assertEquals(
				"main",
				response.getImprovement()
						.getChangeFacts()
						.getFirst()
						.getBefore()
		);
		assertEquals(
				"executeProgram",
				response.getImprovement()
						.getChangeFacts()
						.getFirst()
						.getAfter()
		);
	}

	@Test
	void shouldPreserveAbsentImproveChangeFacts() {
		AiService aiService = mock(AiService.class);

		AppProperties appProperties = new AppProperties(
				"j-aide",
				"0.1.2"
		);

		AiController controller = new AiController(
				aiService,
				appProperties
		);

		ImproveRequest request = new ImproveRequest();
		request.setCode("class Example {}");
		request.setMode("FAST");
		request.setLanguage("java");
		request.setFileName("Example.java");
		request.setLineStart(1);
		request.setLineEnd(1);
		request.setProjectName("demo-project");
		request.setModuleName("demo-module");
		request.setPluginVersion("0.1.2");
		request.setIdeVersion("2025.1");

		StructuredImproveResponse structuredResponse =
				new StructuredImproveResponse();
		structuredResponse.setSummary("No structural changes");
		structuredResponse.setImprovedCode("class Example {}");
		structuredResponse.setChanges(List.of());
		structuredResponse.setRiskHint("none");
		structuredResponse.setConfidence("high");

		AiImproveResult aiResult = new AiImproveResult(
				structuredResponse,
				null,
				"FAST",
				"java",
				false
		);

		when(aiService.improve(
				request.getCode(),
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

		ImproveResponse response = controller.improve(request);

		assertNotNull(response.getImprovement());
		assertNull(response.getImprovement().getChangeFacts());
	}
}
