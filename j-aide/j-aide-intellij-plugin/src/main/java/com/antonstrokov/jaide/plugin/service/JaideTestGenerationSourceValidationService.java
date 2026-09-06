package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.language.JaideLanguageResolver;
import com.intellij.openapi.project.Project;

public class JaideTestGenerationSourceValidationService {

	private final JaideLanguageResolver languageResolver =
			new JaideLanguageResolver();

	private final JaideJavaTestSourceValidationService javaValidationService =
			new JaideJavaTestSourceValidationService();

	public boolean hasSyntaxErrors(
			Project project,
			String fileName,
			String testCode
	) {
		String language = languageResolver.resolve(fileName);

		if (!"java".equals(language)) {
			return false;
		}

		return javaValidationService.hasSyntaxErrors(
				project,
				testCode
		);
	}

	public boolean hasStructuralErrors(
			Project project,
			String fileName,
			String testCode
	) {
		String language = languageResolver.resolve(fileName);

		if (!"java".equals(language)) {
			return false;
		}

		return !javaValidationService.hasTopLevelTypeDeclaration(
				project,
				testCode
		);
	}
}
