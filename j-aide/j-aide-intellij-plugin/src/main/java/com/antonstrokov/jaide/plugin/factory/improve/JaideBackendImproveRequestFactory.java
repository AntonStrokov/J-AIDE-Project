package com.antonstrokov.jaide.plugin.factory.improve;

import com.antonstrokov.jaide.plugin.dto.improve.JaideBackendImproveRequest;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImproveRequest;
import com.antonstrokov.jaide.plugin.language.JaideLanguageResolver;
import com.antonstrokov.jaide.plugin.service.JaidePluginMetadataService;

public class JaideBackendImproveRequestFactory {

	private final JaideLanguageResolver languageResolver = new JaideLanguageResolver();
	private final JaidePluginMetadataService pluginMetadataService = new JaidePluginMetadataService();

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
				pluginMetadataService.getPluginVersion(),
				request.ideVersion()
		);
	}
}
