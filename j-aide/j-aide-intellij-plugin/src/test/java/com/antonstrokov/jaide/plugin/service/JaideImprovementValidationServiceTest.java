package com.antonstrokov.jaide.plugin.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JaideImprovementValidationServiceTest {

	private final JaideImprovementValidationService validationService = new JaideImprovementValidationService();

	@Test
	void shouldTreatDifferentLineEndingsAsNoOpImprovement() {
		String originalCode = "class Example {\r\n    void run() {}\r\n}";
		String improvedCode = "class Example {\n    void run() {}\n}";

		assertTrue(validationService.isNoOpImprovement(originalCode, improvedCode));
	}
}
