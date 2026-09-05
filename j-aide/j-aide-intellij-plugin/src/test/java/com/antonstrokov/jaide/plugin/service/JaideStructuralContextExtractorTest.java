package com.antonstrokov.jaide.plugin.service;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JaideStructuralContextExtractorTest
		extends LightJavaCodeInsightFixtureTestCase5 {

	@Override
	protected String getTestDataPath() {
		return System.getProperty("java.io.tmpdir");
	}

	@Test
	void shouldExtractEnclosingJavaStructureWithoutMethodBodies() {
		String source = """
				package com.example;
				
				class Calculator {
				    int add(int a, int b) {
				        return a + b;
				    }
				
				    int subtract(int a, int b) {
				        return a - b;
				    }
				}
				""";

		PsiFile psiFile = getFixture().configureByText(
				"Calculator.java",
				source
		);
		Document document = getFixture().getDocument(psiFile);

		int methodStart = findRequiredOffset(
				source,
				"int add(int a, int b)",
				0
		);

		int selectionEnd = findRequiredOffset(
				source,
				"\n    }",
				methodStart
		) + "\n    }".length();

		JaideStructuralContextExtractor extractor =
				new JaideStructuralContextExtractor();

		String structuralContext = extractor.extract(
				getFixture().getProject(),
				document,
				methodStart,
				selectionEnd
		);

		assertEquals("""
				package com.example
				class Calculator
				int add(int a, int b)""", structuralContext);

		assertFalse(structuralContext.contains("return a + b"));
		assertFalse(structuralContext.contains("subtract"));
		assertFalse(structuralContext.contains("return a - b"));
	}

	@Test
	void shouldExtractEnclosingJavaStructureWhenSelectionStartsWithIndentation() {
		String source = """
				package com.example;
				
				class Calculator {
				    int add(int a, int b) {
				        return a + b;
				    }
				}
				""";

		PsiFile psiFile = getFixture().configureByText(
				"Calculator.java",
				source
		);
		Document document = getFixture().getDocument(psiFile);

		int methodStart = findRequiredOffset(
				source,
				"int add(int a, int b)",
				0
		);

		int selectionStart =
				source.lastIndexOf('\n', methodStart) + 1;

		int selectionEnd = findRequiredOffset(
				source,
				"\n    }",
				methodStart
		) + "\n    }".length();

		assertEquals(
				' ',
				source.charAt(selectionStart),
				"selection must start on indentation"
		);

		JaideStructuralContextExtractor extractor =
				new JaideStructuralContextExtractor();

		String structuralContext = extractor.extract(
				getFixture().getProject(),
				document,
				selectionStart,
				selectionEnd
		);

		assertEquals("""
				package com.example
				class Calculator
				int add(int a, int b)""", structuralContext);
	}

	@Test
	void shouldOmitPackageLineForJavaDefaultPackage() {
		String source = """
                        class DefaultPackageSmoke {
                            int add(int a, int b) {
                                return a + b;
                            }

                            int subtract(int a, int b) {
                                return a - b;
                            }
                        }
                        """;

		PsiFile psiFile = getFixture().configureByText(
				"DefaultPackageSmoke.java",
				source
		);
		Document document = getFixture().getDocument(psiFile);

		int methodStart = findRequiredOffset(
				source,
				"int add(int a, int b)",
				0
		);

		int selectionEnd = findRequiredOffset(
				source,
				"\n    }",
				methodStart
		) + "\n    }".length();

		JaideStructuralContextExtractor extractor =
				new JaideStructuralContextExtractor();

		String structuralContext = extractor.extract(
				getFixture().getProject(),
				document,
				methodStart,
				selectionEnd
		);

		assertEquals("""
                        class DefaultPackageSmoke
                        int add(int a, int b)""", structuralContext);

		assertFalse(structuralContext.contains("package "));
		assertFalse(structuralContext.contains("subtract"));
		assertFalse(structuralContext.contains("return a - b"));
	}

	private int findRequiredOffset(
			String source,
			String marker,
			int fromIndex
	) {
		int offset = source.indexOf(marker, fromIndex);

		if (offset < 0) {
			fail("Marker was not found in source: " + marker);
		}

		return offset;
	}

	@Test
	void shouldPreserveJavaInterfaceDeclarationKind() {
		String source = """
				package com.example;
				
				interface Calculator {
				    int add(int a, int b);
				}
				""";

		PsiFile psiFile = getFixture().configureByText(
				"Calculator.java",
				source
		);
		Document document = getFixture().getDocument(psiFile);

		int methodStart = findRequiredOffset(
				source,
				"int add(int a, int b)",
				0
		);

		int selectionEnd = methodStart
				+ "int add(int a, int b);".length();

		JaideStructuralContextExtractor extractor =
				new JaideStructuralContextExtractor();

		String structuralContext = extractor.extract(
				getFixture().getProject(),
				document,
				methodStart,
				selectionEnd
		);

		assertEquals("""
				package com.example
				interface Calculator
				int add(int a, int b)""", structuralContext);
	}

	@Test
	void shouldPreserveJavaEnumDeclarationKind() {
		String source = """
				package com.example;
				
				enum Operation {
				    ADD;
				
				    int apply(int a, int b) {
				        return a + b;
				    }
				}
				""";

		PsiFile psiFile = getFixture().configureByText(
				"Operation.java",
				source
		);
		Document document = getFixture().getDocument(psiFile);

		int methodStart = findRequiredOffset(
				source,
				"int apply(int a, int b)",
				0
		);

		int selectionEnd = findRequiredOffset(
				source,
				"\n    }",
				methodStart
		) + "\n    }".length();

		JaideStructuralContextExtractor extractor =
				new JaideStructuralContextExtractor();

		String structuralContext = extractor.extract(
				getFixture().getProject(),
				document,
				methodStart,
				selectionEnd
		);

		assertEquals("""
				package com.example
				enum Operation
				int apply(int a, int b)""", structuralContext);
	}

	@Test
	void shouldPreserveJavaRecordDeclarationKind() {
		String source = """
				package com.example;
				
				record Calculator(int value) {
				    int add(int a, int b) {
				        return a + b;
				    }
				}
				""";

		PsiFile psiFile = getFixture().configureByText(
				"Calculator.java",
				source
		);
		Document document = getFixture().getDocument(psiFile);

		int methodStart = findRequiredOffset(
				source,
				"int add(int a, int b)",
				0
		);

		int selectionEnd = findRequiredOffset(
				source,
				"\n    }",
				methodStart
		) + "\n    }".length();

		JaideStructuralContextExtractor extractor =
				new JaideStructuralContextExtractor();

		String structuralContext = extractor.extract(
				getFixture().getProject(),
				document,
				methodStart,
				selectionEnd
		);

		assertEquals("""
				package com.example
				record Calculator
				int add(int a, int b)""", structuralContext);
	}

	@Test
	void shouldPreserveJavaAnnotationDeclarationKind() {
		String source = """
				package com.example;
				
				@interface Validator {
				    String value();
				}
				""";

		PsiFile psiFile = getFixture().configureByText(
				"Validator.java",
				source
		);
		Document document = getFixture().getDocument(psiFile);

		int methodStart = findRequiredOffset(
				source,
				"String value()",
				0
		);

		int selectionEnd =
				methodStart + "String value();".length();

		JaideStructuralContextExtractor extractor =
				new JaideStructuralContextExtractor();

		String structuralContext = extractor.extract(
				getFixture().getProject(),
				document,
				methodStart,
				selectionEnd
		);

		assertEquals("""
				package com.example
				@interface Validator
				String value()""", structuralContext);
	}

	@Test
	void shouldExtractKotlinStructuralContextWithoutMethodBody() {
		String source = """
				package com.example
				class Calculator {
				    fun add(a: Int, b: Int): Int {
				        return a + b
				    }
				    fun subtract(a: Int, b: Int): Int {
				        return a - b
				    }
				}
				""";

		PsiFile psiFile = getFixture().configureByText(
				"Calculator.kt",
				source
		);
		Document document = getFixture().getDocument(psiFile);

		int methodStart = findRequiredOffset(
				source,
				"fun add(a: Int, b: Int): Int",
				0
		);

		int selectionEnd = findRequiredOffset(
				source,
				"\n    }",
				methodStart
		) + "\n    }".length();

		JaideStructuralContextExtractor extractor =
				new JaideStructuralContextExtractor();

		String structuralContext = extractor.extract(
				getFixture().getProject(),
				document,
				methodStart,
				selectionEnd
		);

		assertFalse(
				structuralContext.isBlank(),
				"Kotlin structural context must not be empty"
		);

		assertTrue(structuralContext.contains("package com.example"));
		assertTrue(structuralContext.contains("Calculator"));
		assertTrue(structuralContext.contains("add"));

		assertFalse(structuralContext.contains("return a + b"));
		assertFalse(structuralContext.contains("subtract"));
		assertFalse(structuralContext.contains("return a - b"));
	}

	@Test
	void shouldExtractStructureFromLatestDocumentState() {
		String source = """
				package com.example;
				class Calculator {
				    int add(int a, int b) {
				        return a + b;
				    }
				}
				""";

		PsiFile psiFile = getFixture().configureByText(
				"Calculator.java",
				source
		);
		Document document = getFixture().getDocument(psiFile);

		String updatedMethod = """
				int multiply(int a, int b) {
				        return a * b;
				    }""";

		int methodStart = findRequiredOffset(
				source,
				"int add(int a, int b)",
				0
		);

		int methodEnd = findRequiredOffset(
				source,
				"\n    }",
				methodStart
		) + "\n    }".length();

		WriteCommandAction.runWriteCommandAction(
				getFixture().getProject(),
				() -> document.replaceString(
						methodStart,
						methodEnd,
						updatedMethod
				)
		);

		int selectionEnd = methodStart + updatedMethod.length();

		JaideStructuralContextExtractor extractor =
				new JaideStructuralContextExtractor();

		String structuralContext = extractor.extract(
				getFixture().getProject(),
				document,
				methodStart,
				selectionEnd
		);

		assertEquals("""
				package com.example
				class Calculator
				int multiply(int a, int b)""", structuralContext);

		assertFalse(structuralContext.contains("return a * b"));
		assertFalse(structuralContext.contains("add"));
	}
}
