package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.config.JaidePreviewLayout;
import com.antonstrokov.jaide.plugin.config.JaideUiColors;
import com.antonstrokov.jaide.plugin.config.JaideUiLabels;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class JaideEmptyStatePanel extends JPanel {

	private final Runnable startWithCodeAction;

	public JaideEmptyStatePanel(Runnable startWithCodeAction) {
		super(new BorderLayout());

		this.startWithCodeAction = startWithCodeAction;

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(JBColor.PanelBackground);
		contentPanel.setBorder(JBUI.Borders.empty(JaidePreviewLayout.CONTENT_PADDING));

		contentPanel.add(createTitleLabel());

		contentPanel.add(createBodyLabel(
				JaideUiLabels.EMPTY_STATE_INTRO
		));

		contentPanel.add(Box.createVerticalStrut(16));
		contentPanel.add(createSectionTitleLabel(
				JaideUiLabels.EMPTY_STATE_CODE_ACTIONS_SECTION
		));
		contentPanel.add(createActionLabel(
				JaideUiLabels.EMPTY_STATE_EXPLAIN_ACTION
		));
		contentPanel.add(createActionLabel(
				JaideUiLabels.EMPTY_STATE_IMPROVE_ACTION
		));
		contentPanel.add(createActionLabel(
				JaideUiLabels.EMPTY_STATE_GENERATE_TESTS_ACTION
		));

		contentPanel.add(Box.createVerticalStrut(16));
		contentPanel.add(createSectionTitleLabel(
				JaideUiLabels.EMPTY_STATE_ERROR_ANALYSIS_SECTION
		));
		contentPanel.add(createBodyLabel(
				JaideUiLabels.EMPTY_STATE_ERROR_HINT
		));
		contentPanel.add(createActionLabel(
				JaideUiLabels.EMPTY_STATE_EXPLAIN_ERROR_ACTION
		));

		contentPanel.add(Box.createVerticalStrut(16));
		contentPanel.add(createSectionTitleLabel(
				JaideUiLabels.EMPTY_STATE_AI_SETUP_SECTION
		));
		contentPanel.add(createBodyLabel(
				JaideUiLabels.EMPTY_STATE_AI_SETUP_HINT
		));

		contentPanel.add(Box.createVerticalStrut(16));
		contentPanel.add(createSectionTitleLabel(
				JaideUiLabels.EMPTY_STATE_NOTE_SECTION
		));
		contentPanel.add(createBodyLabel(
				JaideUiLabels.EMPTY_STATE_RESTART_NOTE
		));

		add(new JBScrollPane(contentPanel), BorderLayout.CENTER);

		add(createStartWithCodeButton(), BorderLayout.SOUTH);
	}

	private JComponent createStartWithCodeButton() {
		JButton button = new JButton(
				JaideUiLabels.START_WITH_CODE_BUTTON
		);

		button.addActionListener(event ->
				startWithCodeAction.run()
		);
		button.setMargin(JBUI.insets(6, 16));
		button.setPreferredSize(JBUI.size(180, 36));

		JPanel buttonPanel =
				new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

		buttonPanel.setOpaque(false);
		buttonPanel.setBorder(
				JBUI.Borders.empty(8, JaidePreviewLayout.CONTENT_PADDING)
		);
		buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
		buttonPanel.add(button);

		return buttonPanel;
	}

	private JBLabel createTitleLabel() {
		JBLabel label = new JBLabel(
				JaideUiLabels.EMPTY_STATE_TITLE
		);
		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 6f));
		label.setForeground(JaideUiColors.PREVIEW_TITLE_FOREGROUND);
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setBorder(JBUI.Borders.emptyBottom(14));
		return label;
	}

	private JBLabel createSectionTitleLabel(String text) {
		JBLabel label = new JBLabel(text.toUpperCase());
		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 2f));
		label.setForeground(JaideUiColors.PREVIEW_SECTION_TITLE_FOREGROUND);
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setBorder(JBUI.Borders.emptyBottom(8));
		return label;
	}

	private JComponent createBodyLabel(String text) {
		JTextArea textArea = new JTextArea(text) {
			@Override
			public Dimension getMaximumSize() {
				Dimension preferredSize = getPreferredSize();

				return new Dimension(
						Integer.MAX_VALUE,
						preferredSize.height
				);
			}
		};
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(false);
		textArea.setForeground(JBColor.foreground());
		textArea.setFont(UIManager.getFont("Label.font"));
		textArea.setAlignmentX(LEFT_ALIGNMENT);
		textArea.setBorder(JBUI.Borders.emptyBottom(6));
		return textArea;
	}

	private JBLabel createActionLabel(String text) {
		JBLabel label = new JBLabel("• " + text);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setForeground(JaideUiColors.PREVIEW_SECTION_TITLE_FOREGROUND);
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setBorder(JBUI.Borders.emptyBottom(4));
		return label;
	}
}
