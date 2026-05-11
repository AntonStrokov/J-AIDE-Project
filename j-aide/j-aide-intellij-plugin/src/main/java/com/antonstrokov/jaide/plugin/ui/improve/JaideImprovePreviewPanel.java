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
		addCodeSection("Original Code", originalCode);
		addCodeSection("Improved Code", improvement.improvedCode());
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

	private void addCodeSection(String title, String code) {
		if (code == null || code.isBlank()) {
			return;
		}

		JBLabel titleLabel = new JBLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		titleLabel.setForeground(JBColor.foreground());
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		titleLabel.setBorder(JBUI.Borders.emptyTop(12));

		JTextArea codeArea = createCodeArea(code);

		JBScrollPane codeScrollPane = new JBScrollPane(codeArea);
		codeScrollPane.setAlignmentX(LEFT_ALIGNMENT);
		codeScrollPane.setBorder(JBUI.Borders.compound(
				JBUI.Borders.customLine(JBColor.border(), 1),
				JBUI.Borders.empty(8)
		));

		contentPanel.add(titleLabel);
		contentPanel.add(Box.createVerticalStrut(4));
		contentPanel.add(codeScrollPane);
	}

	private JTextArea createCodeArea(String code) {
		JTextArea codeArea = new JTextArea(code);
		codeArea.setEditable(false);
		codeArea.setLineWrap(false);
		codeArea.setWrapStyleWord(false);
		codeArea.setFont(
				EditorColorsManager.getInstance()
						.getGlobalScheme()
						.getFont(EditorFontType.PLAIN)
		);
		codeArea.setBackground(JBColor.PanelBackground);
		codeArea.setForeground(JBColor.foreground());
		codeArea.setBorder(JBUI.Borders.empty(0));
		codeArea.setTabSize(4);

		return codeArea;
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
}