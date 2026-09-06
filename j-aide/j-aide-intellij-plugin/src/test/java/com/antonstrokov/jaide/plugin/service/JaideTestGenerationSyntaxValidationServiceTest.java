package com.antonstrokov.jaide.plugin.service;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaideTestGenerationSyntaxValidationServiceTest
		extends LightJavaCodeInsightFixtureTestCase5 {

	@Override
	protected String getTestDataPath() {
		return System.getProperty("java.io.tmpdir");
	}

	@Test
	void shouldDetectSyntaxErrorsForJavaGeneratedTests() {
		JaideTestGenerationSyntaxValidationService validationService =
				new JaideTestGenerationSyntaxValidationService();

		String testCode = """
				class CalculatorTest {
				    void shouldAdd() {
				        int result =
				    }
				}
				""";

		assertTrue(
				validationService.hasSyntaxErrors(
						getFixture().getProject(),
						"CalculatorTest.java",
						testCode
				)
		);
	}

	@Test
	void shouldNotHardBlockUnsupportedLanguageWithJavaParser() {
		JaideTestGenerationSyntaxValidationService validationService =
				new JaideTestGenerationSyntaxValidationService();

		String testCode = """
				class CalculatorTest {
				    fun shouldAdd( {
				}
				""";

		assertFalse(
				validationService.hasSyntaxErrors(
						getFixture().getProject(),
						"CalculatorTest.kt",
						testCode
				)
		);
	}
}
