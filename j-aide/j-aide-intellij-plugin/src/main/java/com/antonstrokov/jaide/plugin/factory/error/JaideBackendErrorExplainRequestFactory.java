package com.antonstrokov.jaide.plugin.factory.error;

import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainRequest;

public class JaideBackendErrorExplainRequestFactory {

	public JaideErrorExplainRequest create(JaideErrorExplainRequest request) {
		return new JaideErrorExplainRequest(
				request.errorText(),
				request.mode(),
				request.language(),
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