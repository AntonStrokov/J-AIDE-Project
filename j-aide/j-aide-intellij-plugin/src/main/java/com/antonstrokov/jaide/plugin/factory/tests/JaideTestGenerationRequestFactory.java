package com.antonstrokov.jaide.plugin.factory.tests;

import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.dto.tests.JaideTestGenerationRequest;
import com.antonstrokov.jaide.plugin.model.JaideExplainMode;

public class JaideTestGenerationRequestFactory {

	public JaideTestGenerationRequest create(JaideEditorContext context) {
		return create(context, "");
	}

	public JaideTestGenerationRequest create(
			JaideEditorContext context,
			String structuralContext
	) {
		return new JaideTestGenerationRequest(
				context.selectedCode(),
				structuralContext,
				"",
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
