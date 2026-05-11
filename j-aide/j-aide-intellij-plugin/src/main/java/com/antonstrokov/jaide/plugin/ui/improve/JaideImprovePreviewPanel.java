package com.antonstrokov.jaide.plugin.ui.improve;

import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class JaideImprovePreviewPanel extends JPanel {

	private final JPanel contentPanel;

	public JaideImprovePreviewPanel(String initialText) {
		super(new BorderLayout());

		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(JBColor.PanelBackground);
		contentPanel.setBorder(JBUI.Borders.empty(12));

		add(new JBScrollPane(contentPanel), BorderLayout.CENTER);

		updateText(initialText);
	}

	public void updateText(String text) {
		contentPanel.removeAll();

		contentPanel.add(createTextArea(text));

		revalidate();
		repaint();
	}

	public void updateImprovement(JaideImprovement improvement, String originalCode) {
		contentPanel.removeAll();

		addTitle("J-Aide Improve Preview");
		addTextSection("Status", "This is a preview only. No files were changed.");
		addTextSection("Summary", improvement.summary());
		addTextSection("Original Code", originalCode);
		addTextSection("Improved Code", improvement.improvedCode());
		addChangesSection(improvement.changes());
		addTextSection("Risk Hint", improvement.riskHint());
		addTextSection("Confidence", improvement.confidence());

		revalidate();
		repaint();
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

	private void addTitle(String title) {
		JBLabel label = new JBLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 2f));
		label.setForeground(JBColor.foreground());
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setBorder(JBUI.Borders.emptyBottom(8));

		contentPanel.add(label);
	}

	private void addTextSection(String title, String value) {
		if (value == null || value.isBlank() || "Not provided".equalsIgnoreCase(value.trim())) {
			return;
		}

		JBLabel titleLabel = new JBLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		titleLabel.setForeground(JBColor.foreground());
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		titleLabel.setBorder(JBUI.Borders.emptyTop(10));

		JTextArea valueArea = createTextArea(value);
		valueArea.setBorder(JBUI.Borders.emptyTop(4));

		contentPanel.add(titleLabel);
		contentPanel.add(valueArea);
	}

	private void addChangesSection(java.util.List<String> changes) {
		if (changes == null || changes.isEmpty()) {
			return;
		}

		StringBuilder result = new StringBuilder();

		for (String change : changes) {
			if (change != null && !change.isBlank()) {
				result.append("• ")
						.append(change)
						.append(System.lineSeparator());
			}
		}

		addTextSection("Changes", result.toString());
	}

	private String formatImprovement(JaideImprovement improvement, String originalCode) {
		StringBuilder result = new StringBuilder();

		result.append("""
				J-Aide Improve Preview
				======================
				
				This is a preview only. No files were changed.
				
				""");

		appendSection(result, "Summary", improvement.summary());
		appendCodeBlock(result, "Original Code", originalCode);
		appendCodeBlock(result, "Improved Code", improvement.improvedCode());
		appendChanges(result, improvement.changes());
		appendSection(result, "Risk Hint", improvement.riskHint());
		appendSection(result, "Confidence", improvement.confidence());

		return result.toString();
	}

	private void appendSection(StringBuilder result, String title, String value) {
		if (value == null || value.isBlank() || "Not provided".equalsIgnoreCase(value.trim())) {
			return;
		}

		result.append(title)
				.append(System.lineSeparator())
				.append("-".repeat(title.length()))
				.append(System.lineSeparator())
				.append(value)
				.append(System.lineSeparator())
				.append(System.lineSeparator());
	}

	private void appendCodeBlock(StringBuilder result, String title, String value) {
		if (value == null || value.isBlank()) {
			return;
		}

		result.append(title)
				.append(System.lineSeparator())
				.append("=".repeat(title.length()))
				.append(System.lineSeparator())
				.append(System.lineSeparator())
				.append(value)
				.append(System.lineSeparator())
				.append(System.lineSeparator());
	}

	private void appendChanges(StringBuilder result, java.util.List<String> changes) {
		if (changes == null || changes.isEmpty()) {
			return;
		}

		result.append("Changes")
				.append(System.lineSeparator())
				.append("-------")
				.append(System.lineSeparator());

		for (String change : changes) {
			if (change != null && !change.isBlank()) {
				result.append("- ")
						.append(change)
						.append(System.lineSeparator());
			}
		}

		result.append(System.lineSeparator());
	}
}