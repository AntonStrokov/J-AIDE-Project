package com.antonstrokov.jaide.plugin.ui.settings;

import com.antonstrokov.jaide.plugin.model.JaideExplainMode;
import com.antonstrokov.jaide.plugin.state.JaideExplainModeState;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;

public class JaideExplainModeSelectorPanel extends JPanel {

	public JaideExplainModeSelectorPanel() {
		super(new FlowLayout(FlowLayout.LEFT, 8, 0));

		setBackground(JBColor.PanelBackground);
		setBorder(JBUI.Borders.emptyBottom(8));

		JLabel label = new JLabel("Code explain mode:");
		label.setToolTipText("Controls FAST / SMART / DEEP mode for Explain Selected Code only.");

		ComboBox<JaideExplainMode> modeComboBox = new ComboBox<>(JaideExplainMode.values());
		modeComboBox.setToolTipText("Controls FAST / SMART / DEEP mode for Explain Selected Code only.");
		modeComboBox.setSelectedItem(JaideExplainModeState.getCurrentMode());
		modeComboBox.addActionListener(event -> {
			Object selectedItem = modeComboBox.getSelectedItem();

			if (selectedItem instanceof JaideExplainMode selectedMode) {
				JaideExplainModeState.setCurrentMode(selectedMode);
			}
		});

		add(label);
		add(modeComboBox);
	}
}
