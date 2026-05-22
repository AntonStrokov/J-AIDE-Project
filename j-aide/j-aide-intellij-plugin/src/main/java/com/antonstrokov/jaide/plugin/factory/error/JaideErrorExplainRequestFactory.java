package com.antonstrokov.jaide.plugin.factory.error;

import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainRequest;
import com.antonstrokov.jaide.plugin.model.JaideRuntimeErrorInput;

public class JaideErrorExplainRequestFactory {

	public JaideErrorExplainRequest create(JaideRuntimeErrorInput input) {
		return new JaideErrorExplainRequest(
				input.errorText(),
				"runtime_error",
				null,
				input.fileName(),
				input.lineStart(),
				input.lineEnd(),
				input.projectName(),
				null,
				input.ideVersion(),
				JaideConstants.PLUGIN_VERSION,
				input.moduleName()
		);
	}
}
