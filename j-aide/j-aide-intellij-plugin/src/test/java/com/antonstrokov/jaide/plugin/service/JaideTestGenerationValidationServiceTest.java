package com.antonstrokov.jaide.plugin.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaideTestGenerationValidationServiceTest {

	private final JaideTestGenerationValidationService validationService =
			new JaideTestGenerationValidationService();

	@Test
	void shouldTreatMissingTestCodeAsBlank() {
		assertTrue(validationService.isBlankTestCode(null));
		assertTrue(validationService.isBlankTestCode(""));
		assertTrue(validationService.isBlankTestCode("   "));
	}

	@Test
	void shouldAcceptNonBlankTestCode() {
		assertFalse(validationService.isBlankTestCode(
				"class ExampleTest {\n\t@Test\n\tvoid shouldRun() {}\n}"
		));
	}

	@Test
	void shouldDetectMarkdownCodeFence() {
		assertTrue(validationService.hasMarkdownCodeFence(
				"```java\nclass ExampleTest {}\n```"
		));
		assertTrue(validationService.hasMarkdownCodeFence(
				"```\nclass ExampleTest {}\n```"
		));
	}

	@Test
	void shouldAcceptTestCodeWithoutMarkdownCodeFence() {
		assertFalse(validationService.hasMarkdownCodeFence(
				"class ExampleTest {\n\t@Test\n\tvoid shouldRun() {}\n}"
		));
	}
}
