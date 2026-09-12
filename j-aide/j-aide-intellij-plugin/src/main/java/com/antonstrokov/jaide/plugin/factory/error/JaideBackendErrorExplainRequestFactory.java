package com.antonstrokov.jaide.plugin.factory.error;

import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainRequest;
import com.antonstrokov.jaide.plugin.language.JaideLanguageResolver;

public class JaideBackendErrorExplainRequestFactory {

	private final JaideLanguageResolver languageResolver =
			new JaideLanguageResolver();

	public JaideErrorExplainRequest create(JaideErrorExplainRequest request) {
		String language = languageResolver.resolve(request.fileName());

		return new JaideErrorExplainRequest(
				request.errorText(),
				request.mode(),
				language,
				request.fileName(),
				request.lineStart(),
				request.lineEnd(),
				request.projectName(),
				request.filePath(),
				request.ideVersion(),
				request.pluginVersion(),
				request.moduleName()
		);
	}
}
