package com.antonstrokov.jaide.plugin.service;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiFile;
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

	@Test
	void shouldDetectUnresolvedSelectedClassReferenceInGeneratedJavaTest() {
		String source = """
				package com.example;

				class Calculator {
				        int add(int a, int b) {
				                return a + b;
				        }
				}
				""";

		PsiFile sourceFile = getFixture().configureByText(
				"Calculator.java",
				source
		);

		Document sourceDocument = getFixture().getDocument(sourceFile);

		int selectionStart =
				source.indexOf("int add(int a, int b)");

		String testCode = """
				package com.tests;

				class CalculatorTest {
				        void shouldAdd() {
				                Calculator calculator = new Calculator();
				        }
				}
				""";

		JaideTestGenerationSourceValidationService validationService =
				new JaideTestGenerationSourceValidationService();

		assertTrue(
				validationService.hasSelectedClassReferenceErrors(
						getFixture().getProject(),
						sourceDocument,
						selectionStart,
						"Calculator.java",
						testCode
				)
		);
	}

	@Test
	void shouldAcceptResolvedSelectedClassReferenceInGeneratedJavaTest() {
		String source = """
				package com.example;

				public class Calculator {
				        public int add(int a, int b) {
				                return a + b;
				        }
				}
				""";

		PsiFile sourceFile = getFixture().configureByText(
				"Calculator.java",
				source
		);

		Document sourceDocument = getFixture().getDocument(sourceFile);

		int selectionStart =
				source.indexOf("public int add(int a, int b)");

		String testCode = """
				package com.tests;

				import com.example.Calculator;

				class CalculatorTest {
				        void shouldAdd() {
				                Calculator calculator = new Calculator();
				        }
				}
				""";

		JaideTestGenerationSourceValidationService validationService =
				new JaideTestGenerationSourceValidationService();

		assertFalse(
				validationService.hasSelectedClassReferenceErrors(
						getFixture().getProject(),
						sourceDocument,
						selectionStart,
						"Calculator.java",
						testCode
				)
		);
	}

	@Test
	void shouldNotRejectGeneratedJavaTestWithoutSelectedClassReference() {
		String source = """
				package com.example;

				class Calculator {
				        int add(int a, int b) {
				                return a + b;
				        }
				}
				""";

		PsiFile sourceFile = getFixture().configureByText(
				"Calculator.java",
				source
		);

		Document sourceDocument = getFixture().getDocument(sourceFile);

		int selectionStart =
				source.indexOf("int add(int a, int b)");

		String testCode = """
				package com.tests;

				class CalculatorTest {
				        void placeholder() {
				        }
				}
				""";

		JaideTestGenerationSourceValidationService validationService =
				new JaideTestGenerationSourceValidationService();

		assertFalse(
				validationService.hasSelectedClassReferenceErrors(
						getFixture().getProject(),
						sourceDocument,
						selectionStart,
						"Calculator.java",
						testCode
				)
		);
	}

	@Test
	void shouldDetectSelectedClassReferenceResolvedToDifferentClass() {
		String source = """
                        package com.example;

                        class Calculator {
                                int add(int a, int b) {
                                        return a + b;
                                }
                        }
                        """;

		PsiFile sourceFile = getFixture().configureByText(
				"Calculator.java",
				source
		);

		Document sourceDocument = getFixture().getDocument(sourceFile);

		int selectionStart =
				source.indexOf("int add(int a, int b)");

		String testCode = """
                        package com.tests;

                        class CalculatorTest {
                                static class Calculator {
                                }

                                void shouldAdd() {
                                        Calculator calculator = new Calculator();
                                }
                        }
                        """;

		JaideTestGenerationSourceValidationService validationService =
				new JaideTestGenerationSourceValidationService();

		assertTrue(
				validationService.hasSelectedClassReferenceErrors(
						getFixture().getProject(),
						sourceDocument,
						selectionStart,
						"Calculator.java",
						testCode
				)
		);
	}
}
