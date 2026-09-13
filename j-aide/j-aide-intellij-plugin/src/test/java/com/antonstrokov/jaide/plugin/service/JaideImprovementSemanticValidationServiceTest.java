package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.dto.improve.JaideImproveChangeFact;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

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

	public void testShouldVerifySingleStructuredJavaMethodRenameFact() {
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
				service.verifyJavaChangeFacts(
						getProject(),
						originalCode,
						improvedCode,
						List.of(
								new JaideImproveChangeFact(
										"rename",
										"method",
										"main",
										"executeProgram"
								)
						)
				);

		assertEquals(
				JaideChangeVerificationResult.CONSISTENT,
				result
		);
	}

	public void testShouldVerifyMultipleConsistentStructuredJavaMethodRenameFacts() {
		JaideImprovementSemanticValidationService service =
				new JaideImprovementSemanticValidationService();

		String originalCode = """
                        class Example {
                            void main() {
                            }

                            int load() {
                                return 1;
                            }
                        }
                        """;

		String improvedCode = """
                        class Example {
                            void executeProgram() {
                            }

                            int loadData() {
                                return 1;
                            }
                        }
                        """;

		JaideChangeVerificationResult result =
				service.verifyJavaChangeFacts(
						getProject(),
						originalCode,
						improvedCode,
						List.of(
								new JaideImproveChangeFact(
										"rename",
										"method",
										"main",
										"executeProgram"
								),
								new JaideImproveChangeFact(
										"rename",
										"method",
										"load",
										"loadData"
								)
						)
				);

		assertEquals(
				JaideChangeVerificationResult.CONSISTENT,
				result
		);
	}

	public void testShouldReturnInconsistentWhenAnyStructuredFactIsInconsistent() {
		JaideImprovementSemanticValidationService service =
				new JaideImprovementSemanticValidationService();

		String originalCode = """
                        class Example {
                            void main() {
                            }

                            int load() {
                                return 1;
                            }
                        }
                        """;

		String improvedCode = """
                        class Example {
                            void executeProgram() {
                            }

                            int load() {
                                return 2;
                            }
                        }
                        """;

		JaideChangeVerificationResult result =
				service.verifyJavaChangeFacts(
						getProject(),
						originalCode,
						improvedCode,
						List.of(
								new JaideImproveChangeFact(
										"rename",
										"method",
										"main",
										"executeProgram"
								),
								new JaideImproveChangeFact(
										"rename",
										"method",
										"load",
										"loadData"
								)
						)
				);

		assertEquals(
				JaideChangeVerificationResult.INCONSISTENT,
				result
		);
	}

	public void testShouldNotVerifyUnsupportedStructuredChangeFact() {
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
				service.verifyJavaChangeFacts(
						getProject(),
						originalCode,
						improvedCode,
						List.of(
								new JaideImproveChangeFact(
										"move",
										"method",
										"main",
										"executeProgram"
								)
						)
				);

		assertEquals(
				JaideChangeVerificationResult.NOT_VERIFIABLE,
				result
		);
	}

	public void testShouldNotVerifyMissingStructuredChangeFacts() {
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

		assertEquals(
				JaideChangeVerificationResult.NOT_VERIFIABLE,
				service.verifyJavaChangeFacts(
						getProject(),
						originalCode,
						improvedCode,
						null
				)
		);

		assertEquals(
				JaideChangeVerificationResult.NOT_VERIFIABLE,
				service.verifyJavaChangeFacts(
						getProject(),
						originalCode,
						improvedCode,
						List.of()
				)
		);
	}
}
