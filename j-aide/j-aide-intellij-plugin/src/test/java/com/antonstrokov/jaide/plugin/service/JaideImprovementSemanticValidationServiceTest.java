package com.antonstrokov.jaide.plugin.service;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class JaideImprovementSemanticValidationServiceTest
		extends BasePlatformTestCase {

	public void testShouldConfirmRealJavaMethodRename() {
		JaideImprovementSemanticValidationService service =
				new JaideImprovementSemanticValidationService();

		String originalCode = """
				class Example {
				    void main() {
				    }
				}
				""";

		String improvedCode = """
				class Example {
				    void executeProgram() {
				    }
				}
				""";

		JaideChangeVerificationResult result =
				service.verifyJavaMethodRename(
						getProject(),
						originalCode,
						improvedCode,
						"main",
						"executeProgram"
				);

		assertEquals(
				JaideChangeVerificationResult.CONSISTENT,
				result
		);
	}

	public void testShouldRejectClaimedRenameWhenOriginalMethodStillExists() {
		JaideImprovementSemanticValidationService service =
				new JaideImprovementSemanticValidationService();

		String originalCode = """
				class Example {
				    void main() {
				        System.out.println("Hello");
				    }
				}
				""";

		String improvedCode = """
				class Example {
				    void main() {
				        System.out.println("Hello, world!");
				    }
				}
				""";

		JaideChangeVerificationResult result =
				service.verifyJavaMethodRename(
						getProject(),
						originalCode,
						improvedCode,
						"main",
						"executeProgram"
				);

		assertEquals(
				JaideChangeVerificationResult.INCONSISTENT,
				result
		);
	}

	public void testShouldNotVerifyAmbiguousOverloadedMethodRename() {
		JaideImprovementSemanticValidationService service =
				new JaideImprovementSemanticValidationService();

		String originalCode = """
				class Example {
				    void main() {
				    }

				    void main(int value) {
				    }
				}
				""";

		String improvedCode = """
				class Example {
				    void executeProgram() {
				    }

				    void main(int value) {
				    }
				}
				""";

		JaideChangeVerificationResult result =
				service.verifyJavaMethodRename(
						getProject(),
						originalCode,
						improvedCode,
						"main",
						"executeProgram"
				);

		assertEquals(
				JaideChangeVerificationResult.NOT_VERIFIABLE,
				result
		);
	}
}
