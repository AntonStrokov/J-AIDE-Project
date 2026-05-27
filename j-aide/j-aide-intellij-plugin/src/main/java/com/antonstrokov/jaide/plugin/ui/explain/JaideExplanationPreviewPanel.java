package com.antonstrokov.jaide.plugin.ui.explain;

import com.antonstrokov.jaide.plugin.config.JaidePreviewLayout;
import com.antonstrokov.jaide.plugin.config.JaideUiColors;
import com.antonstrokov.jaide.plugin.config.JaideUiLabels;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplanation;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class JaideExplanationPreviewPanel extends JPanel {

	private final JPanel contentPanel;

	public JaideExplanationPreviewPanel(String initialText) {
		super(new BorderLayout());

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

	public void updateExplanation(JaideExplanation explanation) {
		contentPanel.removeAll();

		addTitle();
		addTextSection(JaideUiLabels.SUMMARY_SECTION, explanation.summary());
		addTextSection(JaideUiLabels.DETAILS_SECTION, explanation.details());
		addTextSection(JaideUiLabels.COMPLEXITY_SECTION, explanation.complexity());
		addTextSection(JaideUiLabels.SUGGESTION_SECTION, explanation.suggestion());
		addTextSection(JaideUiLabels.BEST_PRACTICE_SECTION, explanation.bestPractice());
		addTextSection(JaideUiLabels.RISK_HINT_SECTION, explanation.riskHint());
		addTextSection(JaideUiLabels.CONFIDENCE_SECTION, explanation.confidence());
		addTextSection(JaideUiLabels.CODE_SMELL_SECTION, explanation.codeSmell());

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
		JBLabel label = new JBLabel(JaideUiLabels.EXPLANATION_TITLE.toUpperCase());
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

	private String normalizeSectionValue(String value) {
		if (value == null || value.isBlank()
				|| JaideUiLabels.NOT_PROVIDED.equalsIgnoreCase(value.trim())) {
			return JaideUiLabels.NOT_PROVIDED_BY_MODEL;
		}

		return value;
	}
}
