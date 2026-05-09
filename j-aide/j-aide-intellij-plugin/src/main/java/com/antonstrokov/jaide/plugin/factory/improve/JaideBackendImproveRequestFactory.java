package com.antonstrokov.jaide.plugin.factory.improve;

import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.dto.improve.JaideBackendImproveRequest;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImproveRequest;
import com.antonstrokov.jaide.plugin.language.JaideLanguageResolver;

public class JaideBackendImproveRequestFactory {

	private final JaideLanguageResolver languageResolver = new JaideLanguageResolver();

	public JaideBackendImproveRequest create(JaideImproveRequest request) {
		String language = languageResolver.resolve(request.fileName());

		return new JaideBackendImproveRequest(
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