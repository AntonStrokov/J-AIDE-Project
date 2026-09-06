package com.antonstrokov.jaide.plugin.service;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaideJavaTestSourceValidationServiceTest
		extends LightJavaCodeInsightFixtureTestCase5 {

	@Override
	protected String getTestDataPath() {
		return System.getProperty("java.io.tmpdir");
	}

	@Test
	void shouldDetectJavaSyntaxError() {
		JaideJavaTestSourceValidationService validationService =
				new JaideJavaTestSourceValidationService();

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
		JaideJavaTestSourceValidationService validationService =
				new JaideJavaTestSourceValidationService();

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

	@Test
	void shouldRejectJavaSourceWithoutTopLevelTypeDeclaration() {
		JaideJavaTestSourceValidationService validationService =
				new JaideJavaTestSourceValidationService();

		String testCode = """
            package com.example;

            import org.junit.jupiter.api.Test;
            """;

		assertFalse(
				validationService.hasTopLevelTypeDeclaration(
						getFixture().getProject(),
						testCode
				)
		);
	}

	@Test
	void shouldDetectTopLevelTypeDeclaration() {
		JaideJavaTestSourceValidationService validationService =
				new JaideJavaTestSourceValidationService();

		String testCode = """
            import org.junit.jupiter.api.Test;

            class CalculatorTest {
                @Test
                void shouldAdd() {
                }
            }
            """;

		assertTrue(
				validationService.hasTopLevelTypeDeclaration(
						getFixture().getProject(),
						testCode
				)
		);
	}
}
