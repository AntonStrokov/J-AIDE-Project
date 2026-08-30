package com.antonstrokov.jaide.plugin.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JaideSurroundingContextExtractorTest {

	private final JaideSurroundingContextExtractor extractor =
			new JaideSurroundingContextExtractor();

	@Test
	void shouldExtractContextAroundSelectionWithoutDuplicatingSelectedCode() {
		String documentText = """
                class Calculator {
                    fun add(a: Int, b: Int): Int {
                        return a + b
                    }
                }
                """;

		String selectedCode = """
                fun add(a: Int, b: Int): Int {
                        return a + b
                    }""";

		int selectionStart = documentText.indexOf(selectedCode);
		int selectionEnd = selectionStart + selectedCode.length();

		String surroundingContext = extractor.extract(
				documentText,
				selectionStart,
				selectionEnd,
				2000
		);

		String expected = documentText.substring(0, selectionStart)
				+ "<SELECTED_CODE>"
				+ documentText.substring(selectionEnd);

		assertEquals(expected, surroundingContext);
	}

	@Test
	void shouldLimitContextAroundSelection() {
		String documentText =
				"0123456789" +
						"SELECTED" +
						"abcdefghij";

		int selectionStart = documentText.indexOf("SELECTED");
		int selectionEnd = selectionStart + "SELECTED".length();

		String surroundingContext = extractor.extract(
				documentText,
				selectionStart,
				selectionEnd,
				10
		);

		assertEquals("56789<SELECTED_CODE>abcde", surroundingContext);
	}

	@Test
	void shouldReturnEmptyContextWhenWholeDocumentIsSelected() {
		String documentText = """
            class Calculator {
            }
            """;

		String surroundingContext = extractor.extract(
				documentText,
				0,
				documentText.length(),
				2000
		);

		assertEquals("", surroundingContext);
	}
}
