package com.antonstrokov.jaide.plugin.factory.tests;

import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.dto.tests.JaideBackendTestGenerationRequest;
import com.antonstrokov.jaide.plugin.dto.tests.JaideTestGenerationRequest;

public class JaideBackendTestGenerationRequestFactory {

	public JaideBackendTestGenerationRequest create(JaideTestGenerationRequest request) {
		return new JaideBackendTestGenerationRequest(
				request.code(),
				request.mode().name(),
				"java",
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
