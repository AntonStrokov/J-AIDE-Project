package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.config.JaideUiColors;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public final class JaideActionButtonFactory {

	private static final int BUTTON_HEIGHT = 28;
	private static final int BUTTON_VERTICAL_MARGIN = 2;
	private static final int BUTTON_HORIZONTAL_MARGIN = 16;
	private static final int BUTTON_BORDER_THICKNESS = 1;

	private JaideActionButtonFactory() {
	}

	public static JButton create(String text) {
		JButton button = new JButton(text) {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();

				return new Dimension(
						preferredSize.width,
						Math.max(
								preferredSize.height,
								JBUI.scale(BUTTON_HEIGHT)
						)
				);
			}
		};

		Border accentBorder = BorderFactory.createLineBorder(
				JaideUiColors.TOOL_WINDOW_BUTTON_ACCENT,
				JBUI.scale(BUTTON_BORDER_THICKNESS),
				true
		);

		Border paddingBorder = JBUI.Borders.empty(
				BUTTON_VERTICAL_MARGIN,
				BUTTON_HORIZONTAL_MARGIN
		);

		button.setBorder(BorderFactory.createCompoundBorder(
				accentBorder,
				paddingBorder
		));

		return button;
	}
}
