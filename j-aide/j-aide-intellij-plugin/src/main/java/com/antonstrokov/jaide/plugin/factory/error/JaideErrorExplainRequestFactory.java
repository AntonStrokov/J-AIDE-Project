package com.antonstrokov.jaide.plugin.factory.error;

import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainRequest;
import com.antonstrokov.jaide.plugin.model.JaideRuntimeErrorInput;
import com.antonstrokov.jaide.plugin.model.JaideRuntimeErrorInputSource;
import com.antonstrokov.jaide.plugin.service.JaidePluginMetadataService;

public class JaideErrorExplainRequestFactory {

	private static final String RUNTIME_ERROR_MODE = "runtime_error";

	private final JaidePluginMetadataService pluginMetadataService =
			new JaidePluginMetadataService();

	public JaideErrorExplainRequest create(JaideRuntimeErrorInput input) {
		boolean consoleSelection =
				input.source() == JaideRuntimeErrorInputSource.CONSOLE_SELECTION;

		return new JaideErrorExplainRequest(
				input.errorText(),
				RUNTIME_ERROR_MODE,
				null,
				consoleSelection ? null : input.fileName(),
				consoleSelection ? null : input.lineStart(),
				consoleSelection ? null : input.lineEnd(),
				input.projectName(),
				null,
				input.ideVersion(),
				pluginMetadataService.getPluginVersion(),
				consoleSelection ? null : input.moduleName()
		);
	}
}
