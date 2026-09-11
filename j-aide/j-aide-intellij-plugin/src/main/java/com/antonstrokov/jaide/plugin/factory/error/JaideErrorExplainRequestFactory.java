package com.antonstrokov.jaide.plugin.factory.error;

import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainRequest;
import com.antonstrokov.jaide.plugin.model.JaideRuntimeErrorInput;
import com.antonstrokov.jaide.plugin.service.JaidePluginMetadataService;

public class JaideErrorExplainRequestFactory {

	private static final String RUNTIME_ERROR_MODE = "runtime_error";

	private final JaidePluginMetadataService pluginMetadataService =
			new JaidePluginMetadataService();

	public JaideErrorExplainRequest create(JaideRuntimeErrorInput input) {
		return new JaideErrorExplainRequest(
				input.errorText(),
				RUNTIME_ERROR_MODE,
				null,
				input.fileName(),
				input.lineStart(),
				input.lineEnd(),
				input.projectName(),
				null,
				input.ideVersion(),
				pluginMetadataService.getPluginVersion(),
				input.moduleName()
		);
	}
}
