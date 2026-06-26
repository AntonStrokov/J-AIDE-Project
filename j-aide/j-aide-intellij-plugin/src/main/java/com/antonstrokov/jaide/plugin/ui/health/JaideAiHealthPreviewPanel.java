package com.antonstrokov.jaide.plugin.ui.health;

import com.antonstrokov.jaide.plugin.config.JaidePreviewLayout;
import com.antonstrokov.jaide.plugin.config.JaideUiColors;
import com.antonstrokov.jaide.plugin.config.JaideUiLabels;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthResponse;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthStatus;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public final class JaideAiHealthPreviewPanel extends JPanel {

	private final JPanel contentPanel;

	public JaideAiHealthPreviewPanel() {
		super(new BorderLayout());

		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(JBColor.PanelBackground);
		contentPanel.setBorder(
				JBUI.Borders.empty(JaidePreviewLayout.CONTENT_PADDING)
		);

		add(new JBScrollPane(contentPanel), BorderLayout.CENTER);
	}

	public void updateHealth(JaideHealthResponse response) {
		contentPanel.removeAll();

		addTitle();
		addTextSection(
				JaideUiLabels.BACKEND_STATUS_SECTION,
				formatStatus(response.backendStatus())
		);
		addTextSection(
				JaideUiLabels.PROVIDER_STATUS_SECTION,
				formatStatus(response.providerStatus())
		);
		addTextSection(
				JaideUiLabels.MODEL_STATUS_SECTION,
				formatStatus(response.modelStatus())
		);
		addTextSection(
				JaideUiLabels.PROVIDER_VERSION_SECTION,
				response.providerVersion()
		);
		addTextSection(
				JaideUiLabels.RESPONSE_TIME_SECTION,
				formatResponseTime(response.responseTimeMs())
		);
		addTextSection(
				JaideUiLabels.HEALTH_MESSAGE_SECTION,
				response.message()
		);
		contentPanel.add(Box.createVerticalGlue());

		revalidate();
		repaint();
	}

	private void addTitle() {
		JBLabel label = new JBLabel(
				JaideUiLabels.AI_HEALTH_PREVIEW_TITLE.toUpperCase()
		);
		label.setFont(label.getFont().deriveFont(
				Font.BOLD,
				label.getFont().getSize()
						+ JaidePreviewLayout.TITLE_FONT_SIZE_DELTA
		));
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
		if (value == null || value.isBlank()) {
			return;
		}

		JBLabel titleLabel = createSectionTitleLabel(title);
		JTextArea valueArea = createTextArea(value);

		valueArea.setBorder(JBUI.Borders.emptyTop(
				JaidePreviewLayout.SECTION_VALUE_TOP_PADDING
		));

		contentPanel.add(titleLabel);
		contentPanel.add(Box.createVerticalStrut(
				JaidePreviewLayout.SECTION_VERTICAL_GAP
		));
		contentPanel.add(valueArea);
	}

	private JBLabel createSectionTitleLabel(String title) {
		JBLabel titleLabel = new JBLabel(title.toUpperCase());
		titleLabel.setFont(titleLabel.getFont().deriveFont(
				Font.BOLD,
				titleLabel.getFont().getSize()
						+ JaidePreviewLayout.SECTION_TITLE_FONT_SIZE_DELTA
		));
		titleLabel.setForeground(
				JaideUiColors.PREVIEW_SECTION_TITLE_FOREGROUND
		);
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
		textArea.setAlignmentX(LEFT_ALIGNMENT);
		Dimension preferredSize = textArea.getPreferredSize();

		textArea.setMaximumSize(new Dimension(
				Integer.MAX_VALUE,
				preferredSize.height
		));

		return textArea;
	}

	private String formatStatus(JaideHealthStatus status) {
		if (status == null) {
			return JaideHealthStatus.UNKNOWN.name();
		}

		return status.name();
	}

	private String formatResponseTime(Long responseTimeMs) {
		if (responseTimeMs == null) {
			return JaideUiLabels.NOT_PROVIDED;
		}

		return responseTimeMs + " ms";
	}
}
