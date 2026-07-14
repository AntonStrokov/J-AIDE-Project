package com.antonstrokov.jaide.plugin.ui.health;

import com.antonstrokov.jaide.plugin.config.JaidePreviewLayout;
import com.antonstrokov.jaide.plugin.config.JaideUiColors;
import com.antonstrokov.jaide.plugin.config.JaideUiLabels;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthResponse;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthStatus;
import com.antonstrokov.jaide.plugin.ui.JaideActionButtonFactory;
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

	public void updateHealth(
			JaideHealthResponse response,
			Runnable retryAction
	) {
		contentPanel.removeAll();

		addTitle();
		addStatusSection(
				JaideUiLabels.BACKEND_STATUS_SECTION,
				response.backendStatus()
		);
		addStatusSection(
				JaideUiLabels.PROVIDER_STATUS_SECTION,
				response.providerStatus()
		);
		addStatusSection(
				JaideUiLabels.MODEL_STATUS_SECTION,
				response.modelStatus()
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

		if (!isFullyReady(response)) {
			addRetryButton(retryAction);
		}

		contentPanel.add(Box.createVerticalGlue());

		revalidate();
		repaint();
	}

	public void showLoading() {
		contentPanel.removeAll();

		addTitle(JaideUiLabels.AI_HEALTH_LOADING_TITLE);
		addTextSection(
				JaideUiLabels.STATUS_SECTION,
				JaideUiLabels.AI_HEALTH_LOADING_MESSAGE
		);
		contentPanel.add(Box.createVerticalGlue());

		revalidate();
		repaint();
	}

	public void showError(
			String message,
			Runnable retryAction
	) {
		contentPanel.removeAll();

		addTitle(JaideUiLabels.AI_HEALTH_ERROR_TITLE);
		addTextSection(
				JaideUiLabels.AI_HEALTH_ERROR_MESSAGE_SECTION,
				message
		);

		addRetryButton(retryAction);

		contentPanel.add(Box.createVerticalGlue());

		revalidate();
		repaint();
	}

	private boolean isFullyReady(JaideHealthResponse response) {
		return response != null
				&& response.backendStatus() == JaideHealthStatus.READY
				&& response.providerStatus() == JaideHealthStatus.READY
				&& response.modelStatus() == JaideHealthStatus.READY;
	}

	private void addRetryButton(Runnable retryAction) {
		if (retryAction == null) {
			return;
		}

		JButton retryButton = JaideActionButtonFactory.create(
				JaideUiLabels.RETRY_BUTTON
		);
		retryButton.setAlignmentX(LEFT_ALIGNMENT);
		retryButton.addActionListener(event -> retryAction.run());

		contentPanel.add(Box.createVerticalStrut(
				JaidePreviewLayout.SECTION_VERTICAL_GAP
		));
		contentPanel.add(retryButton);
	}

	private void addTitle() {
		addTitle(JaideUiLabels.AI_HEALTH_PREVIEW_TITLE);
	}

	private void addTitle(String title) {
		JBLabel label = new JBLabel(title.toUpperCase());
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

	private void addStatusSection(
			String title,
			JaideHealthStatus status
	) {
		JBLabel titleLabel = createSectionTitleLabel(title);
		JTextArea valueArea = createTextArea(formatStatus(status));
		valueArea.setForeground(resolveStatusColor(status));

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

	private JBColor resolveStatusColor(JaideHealthStatus status) {
		if (status == null) {
			return JaideUiColors.HEALTH_STATUS_UNKNOWN_FOREGROUND;
		}

		return switch (status) {
			case READY -> JaideUiColors.HEALTH_STATUS_READY_FOREGROUND;
			case DEGRADED -> JaideUiColors.HEALTH_STATUS_DEGRADED_FOREGROUND;
			case FAILED -> JaideUiColors.HEALTH_STATUS_FAILED_FOREGROUND;
			case UNKNOWN -> JaideUiColors.HEALTH_STATUS_UNKNOWN_FOREGROUND;
		};
	}

	private String formatResponseTime(Long responseTimeMs) {
		if (responseTimeMs == null) {
			return JaideUiLabels.NOT_PROVIDED;
		}

		return responseTimeMs + " ms";
	}
}
