package com.antonstrokov.jaide.plugin.factory.improve;

import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImproveRequest;
import com.antonstrokov.jaide.plugin.model.JaideExplainMode;

public class JaideImproveRequestFactory {

	public JaideImproveRequest create(JaideEditorContext context) {
		return new JaideImproveRequest(
				context.selectedCode(),
				JaideExplainMode.SMART,
				context.fileName(),
				context.lineStart(),
				context.lineEnd(),
				context.projectName(),
				context.moduleName(),
				context.ideVersion()
		);
	}
}