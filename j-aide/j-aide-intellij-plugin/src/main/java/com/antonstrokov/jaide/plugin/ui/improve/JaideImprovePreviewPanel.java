package com.antonstrokov.jaide.plugin.ui.improve;

import com.antonstrokov.jaide.plugin.config.JaideUiLabels;
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
	private static final String CHANGE_ITEM_PREFIX = "• ";
	private static final int TEXT_AREA_TAB_SIZE = 4;
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

		addTitle();
		addTextSection(JaideUiLabels.STATUS_SECTION, JaideUiLabels.IMPROVE_PREVIEW_STATUS);
		addTextSection(JaideUiLabels.SUMMARY_SECTION, improvement.summary());
		addCodeSection(JaideUiLabels.ORIGINAL_CODE_SECTION, originalCode);
		addCodeSection(JaideUiLabels.IMPROVED_CODE_SECTION, improvement.improvedCode());
		addChangesSection(improvement.changes());
		addTextSection(JaideUiLabels.RISK_HINT_SECTION, improvement.riskHint());
		addTextSection(JaideUiLabels.CONFIDENCE_SECTION, improvement.confidence());

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
		textArea.setBorder(JBUI.Borders.empty());
		textArea.setTabSize(TEXT_AREA_TAB_SIZE);
		textArea.setAlignmentX(LEFT_ALIGNMENT);

		return textArea;
	}

	private void addTitle() {
		JBLabel label = new JBLabel(JaideUiLabels.IMPROVE_PREVIEW_TITLE.toUpperCase());
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
		if (value == null || value.isBlank()
				|| JaideUiLabels.NOT_PROVIDED.equalsIgnoreCase(value.trim())) {
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
		codeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		codeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		codeScrollPane.setPreferredSize(new Dimension(
				10,
				calculateCodeBlockViewportHeight(code)
		));
		codeScrollPane.setMaximumSize(new Dimension(
				Integer.MAX_VALUE,
				calculateCodeBlockViewportHeight(code)
		));
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
				calculateCodeContentWidth(code),
				calculateCodeContentHeight(code)
		));

		return codeField;
	}

	private int calculateCodeBlockViewportHeight(String code) {
		int minHeight = 80;
		int maxHeight = 260;

		return Math.clamp(calculateCodeContentHeight(code), minHeight, maxHeight);
	}

	private int calculateCodeContentWidth(String code) {
		int maxLineLength = getMaxLineLength(code);
		int editorFontSize = EditorColorsManager.getInstance()
				.getGlobalScheme()
				.getEditorFontSize();

		int minWidth = 600;
		int maxWidth = 2400;

		return Math.clamp((long) maxLineLength * editorFontSize, minWidth, maxWidth);
	}

	private int calculateCodeContentHeight(String code) {
		int lineCount = code == null || code.isBlank()
				? 1
				: code.split("\\R", -1).length;

		int lineHeight = EditorColorsManager.getInstance()
				.getGlobalScheme()
				.getEditorFontSize() + 8;

		return lineCount * lineHeight + 24;
	}

	private int getMaxLineLength(String code) {
		if (code == null || code.isBlank()) {
			return 1;
		}

		int maxLineLength = 1;

		for (String line : code.split("\\R", -1)) {
			maxLineLength = Math.max(maxLineLength, line.length());
		}

		return maxLineLength;
	}

	private void addChangesSection(java.util.List<String> changes) {
		if (changes == null || changes.isEmpty()) {
			return;
		}

		StringBuilder result = new StringBuilder();

		for (String change : changes) {
			if (change != null && !change.isBlank()) {
				result.append(CHANGE_ITEM_PREFIX)
						.append(change)
						.append(System.lineSeparator());
			}
		}

		addTextSection(JaideUiLabels.CHANGES_SECTION, result.toString());
	}
}
