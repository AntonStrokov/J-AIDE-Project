package com.antonstrokov.jaide.plugin.config;

import com.intellij.ui.JBColor;

import java.awt.Color;

public final class JaideUiColors {

	public static final JBColor PREVIEW_TITLE_FOREGROUND = new JBColor(
			new Color(0x1F2937),
			new Color(0xE6EDF3)
	);

	public static final JBColor PREVIEW_SECTION_TITLE_FOREGROUND = new JBColor(
			new Color(0x0B6EBD),
			new Color(0x7AB7FF)
	);

	public static final JBColor TOOL_WINDOW_BUTTON_ACCENT = new JBColor(
			new Color(0x0B6EBD),
			new Color(0x7AB7FF)
	);

	public static final JBColor HEALTH_STATUS_READY_FOREGROUND = new JBColor(
			new Color(0x15803D),
			new Color(0x4ADE80)
	);

	public static final JBColor HEALTH_STATUS_DEGRADED_FOREGROUND = new JBColor(
			new Color(0xB45309),
			new Color(0xFBBF24)
	);

	public static final JBColor HEALTH_STATUS_FAILED_FOREGROUND = new JBColor(
			new Color(0xB91C1C),
			new Color(0xF87171)
	);

	public static final JBColor HEALTH_STATUS_UNKNOWN_FOREGROUND = new JBColor(
			new Color(0x6B7280),
			new Color(0x9CA3AF)
	);

	private JaideUiColors() {
	}
}
