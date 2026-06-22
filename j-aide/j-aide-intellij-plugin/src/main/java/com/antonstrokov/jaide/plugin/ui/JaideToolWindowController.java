package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.config.JaideToolWindowMode;
import com.intellij.openapi.components.Service;

@Service(Service.Level.PROJECT)
public final class JaideToolWindowController {

	private JaideToolWindowMode currentMode;

	public void setCurrentMode(JaideToolWindowMode currentMode) {
		this.currentMode = currentMode;
	}

	public boolean isShowingErrorExplanation() {
		return currentMode == JaideToolWindowMode.ERROR_EXPLANATION;
	}

	public boolean isShowingTestGeneration() {
		return currentMode == JaideToolWindowMode.TEST_GENERATION;
	}
}
