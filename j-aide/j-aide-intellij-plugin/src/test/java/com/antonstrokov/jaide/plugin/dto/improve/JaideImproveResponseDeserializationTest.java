package com.antonstrokov.jaide.plugin.dto.improve;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JaideImproveResponseDeserializationTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void shouldDeserializeStructuredChangeFacts() throws Exception {
		String json = """
                {
                  "improvement": {
                    "summary": "Improved method name",
                    "improvedCode": "class Example { void executeProgram() {} }",
                    "changes": [
                      "Метод main переименован в executeProgram"
                    ],
                    "changeFacts": [
                      {
                        "operation": "rename",
                        "symbolKind": "method",
                        "before": "main",
                        "after": "executeProgram"
                      }
                    ],
                    "riskHint": "low",
                    "confidence": "high"
                  },
                  "success": true
                }
                """;

		JaideImproveResponse response =
				objectMapper.readValue(
						json,
						JaideImproveResponse.class
				);

		assertNotNull(response.improvement());
		assertNotNull(response.improvement().changeFacts());
		assertEquals(
				1,
				response.improvement().changeFacts().size()
		);

		JaideImproveChangeFact changeFact =
				response.improvement().changeFacts().getFirst();

		assertEquals("rename", changeFact.operation());
		assertEquals("method", changeFact.symbolKind());
		assertEquals("main", changeFact.before());
		assertEquals("executeProgram", changeFact.after());
	}

	@Test
	void shouldAllowMissingStructuredChangeFacts() throws Exception {
		String json = """
            {
              "improvement": {
                "summary": "Improved code",
                "improvedCode": "class Example {}",
                "changes": [],
                "riskHint": "none",
                "confidence": "high"
              },
              "success": true
            }
            """;

		JaideImproveResponse response =
				objectMapper.readValue(
						json,
						JaideImproveResponse.class
				);

		assertNotNull(response.improvement());
		assertNull(response.improvement().changeFacts());
	}
}
