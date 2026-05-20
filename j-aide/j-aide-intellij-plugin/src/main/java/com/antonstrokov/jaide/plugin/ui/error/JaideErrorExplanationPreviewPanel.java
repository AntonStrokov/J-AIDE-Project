package com.antonstrokov.jaide.plugin.ui.error;

import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplanation;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JaideErrorExplanationPreviewPanel extends JPanel {

	private final JPanel contentPanel;

	public JaideErrorExplanationPreviewPanel() {
		super(new BorderLayout());

		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(JBColor.PanelBackground);
		contentPanel.setBorder(JBUI.Borders.empty(12));

		add(new JBScrollPane(contentPanel), BorderLayout.CENTER);
	}

	public void updateErrorExplanation(JaideErrorExplanation explanation) {
		contentPanel.removeAll();

		addTitle("J-Aide Runtime Error Explanation");
		addTextSection("Summary", explanation.summary());
		addTextSection("Likely Cause", explanation.likelyCause());
		addTextSection("Where To Look", explanation.whereToLook());
		addTextSection("Suggested Fixes", normalizeList(explanation.suggestedFixes()));
		addTextSection("Risk Hint", explanation.riskHint());
		addTextSection("Confidence", explanation.confidence());

		revalidate();
		repaint();
	}

	private void addTitle(String title) {
		JBLabel label = new JBLabel(title.toUpperCase());
		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 4f));
		label.setForeground(new JBColor(
				new Color(0x1F2937),
				new Color(0xE6EDF3)
		));
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setBorder(JBUI.Borders.empty(4, 0, 22, 0));

		contentPanel.add(label);
	}

	private void addTextSection(String title, String value) {
		JBLabel titleLabel = createSectionTitleLabel(title);

		JTextArea valueArea = createTextArea(normalizeSectionValue(value));
		valueArea.setBorder(JBUI.Borders.emptyTop(4));

		contentPanel.add(titleLabel);
		contentPanel.add(Box.createVerticalStrut(4));
		contentPanel.add(valueArea);
	}

	private JBLabel createSectionTitleLabel(String title) {
		JBLabel titleLabel = new JBLabel(title.toUpperCase());
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, titleLabel.getFont().getSize() + 1.5f));
		titleLabel.setForeground(new JBColor(
				new Color(0x0B6EBD),
				new Color(0x7AB7FF)
		));
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		titleLabel.setBorder(JBUI.Borders.empty(18, 0, 8, 0));

		return titleLabel;
	}

	private JTextArea createTextArea(String text) {
		JTextArea textArea = new JTextArea(text);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(
				EditorColorsManager.getInstance()
						.getGlobalScheme()
						.getFont(EditorFontType.PLAIN)
		);
		textArea.setBackground(JBColor.PanelBackground);
		textArea.setForeground(JBColor.foreground());
		textArea.setBorder(JBUI.Borders.empty(0));
		textArea.setTabSize(4);
		textArea.setAlignmentX(LEFT_ALIGNMENT);

		return textArea;
	}

	private String normalizeSectionValue(String value) {
		if (value == null || value.isBlank() || "Not provided".equalsIgnoreCase(value.trim())) {
			return "Not provided by model.";
		}

		return value;
	}

	private String normalizeList(List<String> values) {
		if (values == null || values.isEmpty()) {
			return "Not provided by model.";
		}

		StringBuilder builder = new StringBuilder();

		for (String value : values) {
			if (value != null && !value.isBlank()) {
				builder.append("- ").append(value).append(System.lineSeparator());
			}
		}

		if (builder.isEmpty()) {
			return "Not provided by model.";
		}

		return builder.toString();
	}
}