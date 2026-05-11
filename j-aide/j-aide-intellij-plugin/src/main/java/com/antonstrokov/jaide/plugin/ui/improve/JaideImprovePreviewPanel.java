package com.antonstrokov.jaide.plugin.ui.improve;

import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;

import javax.swing.JTextArea;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class JaideImprovePreviewPanel extends JPanel {

	private final JTextArea textArea;

	public JaideImprovePreviewPanel(String initialText) {
		super(new BorderLayout());

		textArea = new JTextArea(initialText);
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
		textArea.setBorder(JBUI.Borders.empty(12));
		textArea.setTabSize(4);

		add(new JBScrollPane(textArea), BorderLayout.CENTER);
	}

	public void updateText(String text) {
		textArea.setText(text);
		textArea.setCaretPosition(0);
	}

	public void updateImprovement(JaideImprovement improvement, String originalCode) {
		updateText(formatImprovement(improvement, originalCode));
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