package com.antonstrokov.jaide.plugin.ui.error;

import com.antonstrokov.jaide.plugin.config.JaidePreviewLayout;
import com.antonstrokov.jaide.plugin.config.JaideUiColors;
import com.antonstrokov.jaide.plugin.config.JaideUiLabels;
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

	private static final String LIST_ITEM_PREFIX = "- ";
	private final JPanel contentPanel;

	public JaideErrorExplanationPreviewPanel() {
		super(new BorderLayout());

		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(JBColor.PanelBackground);
		contentPanel.setBorder(JBUI.Borders.empty(JaidePreviewLayout.CONTENT_PADDING));

		add(new JBScrollPane(contentPanel), BorderLayout.CENTER);
	}

	public void updateErrorExplanation(JaideErrorExplanation explanation) {
		contentPanel.removeAll();

		addTitle();
		addTextSection(JaideUiLabels.SUMMARY_SECTION, explanation.summary());
		addTextSection(JaideUiLabels.LIKELY_CAUSE_SECTION, explanation.likelyCause());
		addTextSection(JaideUiLabels.WHERE_TO_LOOK_SECTION, explanation.whereToLook());
		addTextSection(JaideUiLabels.SUGGESTED_FIXES_SECTION, normalizeList(explanation.suggestedFixes()));
		addTextSection(JaideUiLabels.RISK_HINT_SECTION, explanation.riskHint());
		addTextSection(JaideUiLabels.CONFIDENCE_SECTION, explanation.confidence());

		revalidate();
		repaint();
	}

	private void addTitle() {
		JBLabel label = new JBLabel(JaideUiLabels.RUNTIME_ERROR_EXPLANATION_TITLE.toUpperCase());
		label.setFont(label.getFont()
				.deriveFont(Font.BOLD,
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

	private void addTextSection(String title, String value) {
		JBLabel titleLabel = createSectionTitleLabel(title);

		JTextArea valueArea = createTextArea(normalizeSectionValue(value));
		valueArea.setBorder(JBUI.Borders.emptyTop(JaidePreviewLayout.SECTION_VALUE_TOP_PADDING));

		contentPanel.add(titleLabel);
		contentPanel.add(Box.createVerticalStrut(JaidePreviewLayout.SECTION_VERTICAL_GAP));
		contentPanel.add(valueArea);
	}

	private JBLabel createSectionTitleLabel(String title) {
		JBLabel titleLabel = new JBLabel(title.toUpperCase());
		titleLabel.setFont(titleLabel.getFont().deriveFont(
				Font.BOLD,
				titleLabel.getFont().getSize() + JaidePreviewLayout.SECTION_TITLE_FONT_SIZE_DELTA
		));
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

	private String normalizeSectionValue(String value) {
		if (value == null || value.isBlank()
				|| JaideUiLabels.NOT_PROVIDED.equalsIgnoreCase(value.trim())) {
			return JaideUiLabels.NOT_PROVIDED_BY_MODEL;
		}

		return value;
	}

	private String normalizeList(List<String> values) {
		if (values == null || values.isEmpty()) {
			return JaideUiLabels.NOT_PROVIDED_BY_MODEL;
		}

		StringBuilder builder = new StringBuilder();

		for (String value : values) {
			if (value != null && !value.isBlank()) {
				builder.append(LIST_ITEM_PREFIX).append(value).append(System.lineSeparator());
			}
		}

		if (builder.isEmpty()) {
			return JaideUiLabels.NOT_PROVIDED_BY_MODEL;
		}

		return builder.toString();
	}
}
