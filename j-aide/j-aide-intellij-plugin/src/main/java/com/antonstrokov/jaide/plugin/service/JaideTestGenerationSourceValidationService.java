package com.antonstrokov.jaide.plugin.service;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;

public class JaideTestGenerationSourceValidationService {

	private final JaideJavaTestSourceValidationService javaValidationService =
			new JaideJavaTestSourceValidationService();

	public boolean hasSyntaxErrors(
			Project project,
			String sourceLanguage,
			String testCode
	) {

		if (!"java".equals(sourceLanguage)) {
			return false;
		}

		return javaValidationService.hasSyntaxErrors(
				project,
				testCode
		);
	}

	public boolean hasStructuralErrors(
			Project project,
			String sourceLanguage,
			String testCode
	) {

		if (!"java".equals(sourceLanguage)) {
			return false;
		}

		return !javaValidationService.hasTopLevelTypeDeclaration(
				project,
				testCode
		);
	}

	public boolean hasSelectedClassReferenceErrors(
			Project project,
			Document sourceDocument,
			int selectionStart,
			String sourceLanguage,
			String testCode
	) {

		if (!"java".equals(sourceLanguage)) {
			return false;
		}

		return javaValidationService.hasSelectedClassReferenceErrors(
				project,
				sourceDocument,
				selectionStart,
				testCode
		);
	}
}
