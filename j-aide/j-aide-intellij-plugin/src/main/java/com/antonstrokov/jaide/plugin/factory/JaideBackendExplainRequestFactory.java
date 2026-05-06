package com.antonstrokov.jaide.plugin.factory;

import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.dto.JaideBackendExplainRequest;
import com.antonstrokov.jaide.plugin.dto.JaideExplainRequest;
import com.antonstrokov.jaide.plugin.language.JaideLanguageResolver;

public class JaideBackendExplainRequestFactory {

	private final JaideLanguageResolver languageResolver = new JaideLanguageResolver();

	public JaideBackendExplainRequest create(JaideExplainRequest request) {
		String language = languageResolver.resolve(request.fileName());

		return new JaideBackendExplainRequest(
				request.code(),
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