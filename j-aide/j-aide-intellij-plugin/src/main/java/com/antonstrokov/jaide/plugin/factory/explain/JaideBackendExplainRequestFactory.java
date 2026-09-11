package com.antonstrokov.jaide.plugin.factory.explain;

import com.antonstrokov.jaide.plugin.dto.backend.JaideBackendExplainRequest;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplainRequest;
import com.antonstrokov.jaide.plugin.language.JaideLanguageResolver;
import com.antonstrokov.jaide.plugin.service.JaidePluginMetadataService;

public class JaideBackendExplainRequestFactory {

	private final JaideLanguageResolver languageResolver = new JaideLanguageResolver();
	private final JaidePluginMetadataService pluginMetadataService = new JaidePluginMetadataService();

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
				pluginMetadataService.getPluginVersion(),
				request.ideVersion()
		);
	}
}
