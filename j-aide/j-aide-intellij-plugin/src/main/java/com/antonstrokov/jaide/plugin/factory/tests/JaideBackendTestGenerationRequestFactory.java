package com.antonstrokov.jaide.plugin.factory.tests;

import com.antonstrokov.jaide.plugin.dto.tests.JaideBackendTestGenerationRequest;
import com.antonstrokov.jaide.plugin.dto.tests.JaideTestGenerationRequest;
import com.antonstrokov.jaide.plugin.service.JaidePluginMetadataService;

public class JaideBackendTestGenerationRequestFactory {

	private final JaidePluginMetadataService pluginMetadataService =
			new JaidePluginMetadataService();

	public JaideBackendTestGenerationRequest create(JaideTestGenerationRequest request) {
		String language = request.sourceLanguage();

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
				pluginMetadataService.getPluginVersion(),
				request.ideVersion()
		);
	}
}
