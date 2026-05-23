package com.antonstrokov.jaide.plugin.factory.explain;

import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplainRequest;
import com.antonstrokov.jaide.plugin.state.JaideExplainModeState;

public class JaideExplainRequestFactory {

	public JaideExplainRequest create(JaideEditorContext context) {
		return new JaideExplainRequest(
				context.selectedCode(),
				JaideExplainModeState.getCurrentMode(),
				context.fileName(),
				context.lineStart(),
				context.lineEnd(),
				context.projectName(),
				context.moduleName(),
				context.ideVersion()
		);
	}
}