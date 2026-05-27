package com.antonstrokov.jaide.plugin.ui.improve;

import com.antonstrokov.jaide.plugin.config.JaidePreviewLayout;
import com.antonstrokov.jaide.plugin.config.JaideUiColors;
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
	private static final int CODE_SCROLL_PREFERRED_WIDTH = 10;
	private static final int CODE_BLOCK_BORDER_WIDTH = 1;
	private static final int CODE_BLOCK_PADDING = 8;
	private static final int CODE_BLOCK_MIN_HEIGHT = 80;
	private static final int CODE_BLOCK_MAX_HEIGHT = 260;
	private static final int CODE_CONTENT_MIN_WIDTH = 600;
	private static final int CODE_CONTENT_MAX_WIDTH = 2400;
	private static final int DEFAULT_LINE_COUNT = 1;
	private static final int DEFAULT_MAX_LINE_LENGTH = 1;
	private static final int CODE_LINE_HEIGHT_PADDING = 8;
	private static final int CODE_CONTENT_HEIGHT_PADDING = 24;
	private final Project project;
	private final JPanel contentPanel;

	public JaideImprovePreviewPanel(Project project, String initialText) {
		super(new BorderLayout());

		this.project = project;

		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(JBColor.PanelBackground);
		contentPanel.setBorder(JBUI.Borders.empty(JaidePreviewLayout.CONTENT_PADDING));

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
		textArea.setTabSize(JaidePreviewLayout.TEXT_AREA_TAB_SIZE);
		textArea.setAlignmentX(LEFT_ALIGNMENT);

		return textArea;
	}

	private void addTitle() {
		JBLabel label = new JBLabel(JaideUiLabels.IMPROVE_PREVIEW_TITLE.toUpperCase());
		label.setFont(label.getFont().deriveFont(
				Font.BOLD,
				label.getFont().getSize() + JaidePreviewLayout.TITLE_FONT_SIZE_DELTA));
		label.setForeground(JaideUiColors.PREVIEW_TITLE_FOREGROUND);
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setBorder(JBUI.Borders.empty(
				JaidePreviewLayout.TITLE_BORDER_TOP,
				JaidePreviewLayout.TITLE_BORDER_LEFT,
				JaidePreviewLayout.TITLE_BORDER_BOTTOM,
				JaidePreviewLayout.TITLE_BORDER_RIGHT
		));

		contentPanel.add(label);
	}

	private JBLabel createSectionTitleLabel(String title) {
		JBLabel titleLabel = new JBLabel(title.toUpperCase());
		titleLabel.setFont(titleLabel.getFont().deriveFont(
				Font.BOLD,
				titleLabel.getFont().getSize() + JaidePreviewLayout.SECTION_TITLE_FONT_SIZE_DELTA));
		titleLabel.setForeground(JaideUiColors.PREVIEW_SECTION_TITLE_FOREGROUND);
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		titleLabel.setBorder(JBUI.Borders.empty(
				JaidePreviewLayout.SECTION_TITLE_BORDER_TOP,
				JaidePreviewLayout.SECTION_TITLE_BORDER_LEFT,
				JaidePreviewLayout.SECTION_TITLE_BORDER_BOTTOM,
				JaidePreviewLayout.SECTION_TITLE_BORDER_RIGHT
		));

		return titleLabel;
	}

	private void addTextSection(String title, String value) {
		if (value == null || value.isBlank()
				|| JaideUiLabels.NOT_PROVIDED.equalsIgnoreCase(value.trim())) {
			return;
		}

		JBLabel titleLabel = createSectionTitleLabel(title);

		JTextArea valueArea = createTextArea(value);
		valueArea.setBorder(JBUI.Borders.emptyTop(JaidePreviewLayout.SECTION_VALUE_TOP_PADDING));

		contentPanel.add(titleLabel);
		contentPanel.add(Box.createVerticalStrut(JaidePreviewLayout.SECTION_VERTICAL_GAP));
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
				CODE_SCROLL_PREFERRED_WIDTH,
				calculateCodeBlockViewportHeight(code)
		));
		codeScrollPane.setMaximumSize(new Dimension(
				Integer.MAX_VALUE,
				calculateCodeBlockViewportHeight(code)
		));
		codeScrollPane.setBorder(JBUI.Borders.compound(
				JBUI.Borders.customLine(JBColor.border(), CODE_BLOCK_BORDER_WIDTH),
				JBUI.Borders.empty(CODE_BLOCK_PADDING)
		));

		contentPanel.add(titleLabel);
		contentPanel.add(Box.createVerticalStrut(JaidePreviewLayout.SECTION_VERTICAL_GAP));
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
		return Math.clamp(
				calculateCodeContentHeight(code),
				CODE_BLOCK_MIN_HEIGHT,
				CODE_BLOCK_MAX_HEIGHT
		);
	}

	private int calculateCodeContentWidth(String code) {
		int maxLineLength = getMaxLineLength(code);
		int editorFontSize = EditorColorsManager.getInstance()
				.getGlobalScheme()
				.getEditorFontSize();

		return Math.clamp(
				(long) maxLineLength * editorFontSize,
				CODE_CONTENT_MIN_WIDTH,
				CODE_CONTENT_MAX_WIDTH
		);
	}

	private int calculateCodeContentHeight(String code) {
		int lineCount = code == null || code.isBlank()
				? DEFAULT_LINE_COUNT
				: code.split("\\R", -1).length;

		int lineHeight = EditorColorsManager.getInstance()
				.getGlobalScheme()
				.getEditorFontSize() + CODE_LINE_HEIGHT_PADDING;

		return lineCount * lineHeight + CODE_CONTENT_HEIGHT_PADDING;
	}

	private int getMaxLineLength(String code) {
		if (code == null || code.isBlank()) {
			return DEFAULT_MAX_LINE_LENGTH;
		}

		int maxLineLength = DEFAULT_MAX_LINE_LENGTH;

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
