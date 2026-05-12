package com.antonstrokov.jaide.plugin.ui.improve;

import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class JaideImprovePreviewPanel extends JPanel {
	private final Project project;
	private final JPanel contentPanel;

	public JaideImprovePreviewPanel(Project project, String initialText) {
		super(new BorderLayout());

		this.project = project;

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

	private void addTextSection(String title, String value) {
		if (value == null || value.isBlank() || "Not provided".equalsIgnoreCase(value.trim())) {
			return;
		}

		JBLabel titleLabel = createSectionTitleLabel(title);

		JTextArea valueArea = createTextArea(value);
		valueArea.setBorder(JBUI.Borders.emptyTop(4));

		contentPanel.add(titleLabel);
		contentPanel.add(Box.createVerticalStrut(4));
		contentPanel.add(valueArea);
	}

	private void addCodeSection(String title, String code) {
		if (code == null || code.isBlank()) {
			return;
		}

		JBLabel titleLabel = createSectionTitleLabel(title);

		EditorTextField codeField = createCodeField(code);

		JBScrollPane codeScrollPane = new JBScrollPane(codeField);
		codeScrollPane.setAlignmentX(LEFT_ALIGNMENT);
		codeScrollPane.setBorder(JBUI.Borders.compound(
				JBUI.Borders.customLine(JBColor.border(), 1),
				JBUI.Borders.empty(8)
		));

		contentPanel.add(titleLabel);
		contentPanel.add(Box.createVerticalStrut(4));
		contentPanel.add(codeScrollPane);
	}

	private EditorTextField createCodeField(String code) {
		FileType javaFileType = FileTypeManager.getInstance().getFileTypeByExtension("java");

		EditorTextField codeField = new EditorTextField(
				EditorFactory.getInstance().createDocument(code),
				project,
				javaFileType,
				true,
				false
		);

		codeField.setOneLineMode(false);
		codeField.setViewer(true);
		codeField.setFontInheritedFromLAF(false);
		codeField.setAlignmentX(LEFT_ALIGNMENT);
		codeField.setPreferredSize(new Dimension(
				10,
				calculateCodeBlockHeight(code)
		));

		return codeField;
	}

	private int calculateCodeBlockHeight(String code) {
		int lineCount = code == null || code.isBlank()
				? 1
				: code.split("\\R", -1).length;

		int lineHeight = EditorColorsManager.getInstance()
				.getGlobalScheme()
				.getEditorFontSize() + 8;

		int minHeight = 80;
		int maxHeight = 260;

		return Math.max(minHeight, Math.min(maxHeight, lineCount * lineHeight + 24));
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