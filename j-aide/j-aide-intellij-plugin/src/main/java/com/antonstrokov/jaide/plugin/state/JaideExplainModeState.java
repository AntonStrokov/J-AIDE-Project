package com.antonstrokov.jaide.plugin.state;

import com.antonstrokov.jaide.plugin.model.JaideExplainMode;

public final class JaideExplainModeState {

	private static JaideExplainMode currentMode = JaideExplainMode.SMART;

	private JaideExplainModeState() {
	}

	public static JaideExplainMode getCurrentMode() {
		return currentMode;
	}

	public static void setCurrentMode(JaideExplainMode mode) {
		if (mode == null) {
			currentMode = JaideExplainMode.SMART;
			return;
		}

		currentMode = mode;
	}
}