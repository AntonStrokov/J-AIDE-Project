package com.antonstrokov.jaide.plugin.factory.tests;

import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.dto.tests.JaideBackendTestGenerationRequest;
import com.antonstrokov.jaide.plugin.dto.tests.JaideTestGenerationRequest;
import com.antonstrokov.jaide.plugin.language.JaideLanguageResolver;

public class JaideBackendTestGenerationRequestFactory {
	private final JaideLanguageResolver languageResolver = new JaideLanguageResolver();

	public JaideBackendTestGenerationRequest create(JaideTestGenerationRequest request) {
		String language = languageResolver.resolve(request.fileName());

		return new JaideBackendTestGenerationRequest(
				request.code(),
				request.structuralContext(),
				request.surroundingContext(),
				request.mode().name(),
				language,
				request.fileName(),
				request.lineStart(),
				request.lineEnd(),
				request.projectName(),
				request.moduleName(),
				JaideConstants.PLUGIN_VERSION,
				request.ideVersion()
		);
	}
}
