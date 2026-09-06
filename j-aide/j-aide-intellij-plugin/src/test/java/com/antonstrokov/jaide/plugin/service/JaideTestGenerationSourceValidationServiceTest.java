package com.antonstrokov.jaide.plugin.service;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaideTestGenerationSourceValidationServiceTest
		extends LightJavaCodeInsightFixtureTestCase5 {

	@Override
	protected String getTestDataPath() {
		return System.getProperty("java.io.tmpdir");
	}

	@Test
	void shouldDetectSyntaxErrorsForJavaGeneratedTests() {
		JaideTestGenerationSourceValidationService validationService =
				new JaideTestGenerationSourceValidationService();

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
		JaideTestGenerationSourceValidationService validationService =
				new JaideTestGenerationSourceValidationService();

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

	@Test
	void shouldDetectMissingTopLevelTypeForJavaGeneratedTests() {
		JaideTestGenerationSourceValidationService validationService =
				new JaideTestGenerationSourceValidationService();

		String testCode = """
            package com.example;

            import org.junit.jupiter.api.Test;
            """;

		assertTrue(
				validationService.hasStructuralErrors(
						getFixture().getProject(),
						"CalculatorTest.java",
						testCode
				)
		);
	}
}
