package com.antonstrokov.jaide.plugin.state;

import com.antonstrokov.jaide.plugin.model.JaideExplainMode;
import com.intellij.ide.util.PropertiesComponent;

public final class JaideExplainModeState {
	private static final String EXPLAIN_MODE_KEY = "j-aide.explain.mode";

	private JaideExplainModeState() {
	}

	public static JaideExplainMode getCurrentMode() {
		String modeName = PropertiesComponent.getInstance().getValue(EXPLAIN_MODE_KEY);

		if (modeName == null || modeName.isBlank()) {
			return JaideExplainMode.SMART;
		}

		try {
			return JaideExplainMode.valueOf(modeName);
		} catch (IllegalArgumentException ex) {
			return JaideExplainMode.SMART;
		}
	}

	public static void setCurrentMode(JaideExplainMode mode) {
		JaideExplainMode modeToStore = mode == null
				? JaideExplainMode.SMART
				: mode;

		PropertiesComponent.getInstance().setValue(EXPLAIN_MODE_KEY, modeToStore.name());
	}
}
