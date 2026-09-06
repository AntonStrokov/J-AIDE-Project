package com.antonstrokov.jaide.plugin.service;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaideJavaTestSyntaxValidationServiceTest
		extends LightJavaCodeInsightFixtureTestCase5 {

	@Override
	protected String getTestDataPath() {
		return System.getProperty("java.io.tmpdir");
	}

	@Test
	void shouldDetectJavaSyntaxError() {
		JaideJavaTestSyntaxValidationService validationService =
				new JaideJavaTestSyntaxValidationService();

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
						testCode
				)
		);
	}

	@Test
	void shouldAcceptSyntacticallyValidJavaSource() {
		JaideJavaTestSyntaxValidationService validationService =
				new JaideJavaTestSyntaxValidationService();

		String testCode = """
				class CalculatorTest {
				    void shouldAdd() {
				        int result = 2 + 2;
				    }
				}
				""";

		assertFalse(
				validationService.hasSyntaxErrors(
						getFixture().getProject(),
						testCode
				)
		);
	}
}
