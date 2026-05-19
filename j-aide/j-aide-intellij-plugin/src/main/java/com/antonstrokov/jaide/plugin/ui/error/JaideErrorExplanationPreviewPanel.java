package com.antonstrokov.jaide.plugin.ui.error;

import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplanation;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JaideErrorExplanationPreviewPanel extends JPanel {

	private final JPanel contentPanel;

	public JaideErrorExplanationPreviewPanel() {
		super(new BorderLayout());

		this.contentPanel = new JPanel();
		this.contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		this.contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(scrollPane, BorderLayout.CENTER);
	}

	public void updateErrorExplanation(JaideErrorExplanation explanation) {
		contentPanel.removeAll();

		addTitle("RUNTIME ERROR EXPLANATION");

		addSection("Summary", explanation.summary());
		addSection("Likely Cause", explanation.likelyCause());
		addSection("Where To Look", explanation.whereToLook());
		addListSection("Suggested Fixes", explanation.suggestedFixes());
		addSection("Risk Hint", explanation.riskHint());
		addSection("Confidence", explanation.confidence());

		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private void addTitle(String text) {
		JLabel label = new JLabel(text);
		label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
		label.setAlignmentX(Component.LEFT_ALIGNMENT);

		contentPanel.add(label);
		contentPanel.add(Box.createVerticalStrut(12));
	}

	private void addSection(String title, String value) {
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 13f));
		titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JTextArea textArea = new JTextArea(normalize(value));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(false);
		textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
		textArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));

		contentPanel.add(titleLabel);
		contentPanel.add(textArea);
	}

	private void addListSection(String title, List<String> values) {
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 13f));
		titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JTextArea textArea = new JTextArea(normalizeList(values));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(false);
		textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
		textArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));

		contentPanel.add(titleLabel);
		contentPanel.add(textArea);
	}

	private String normalize(String value) {
		if (value == null || value.isBlank()) {
			return "Not provided by model.";
		}

		return value;
	}

	private String normalizeList(List<String> values) {
		if (values == null || values.isEmpty()) {
			return "Not provided by model.";
		}

		StringBuilder builder = new StringBuilder();

		for (String value : values) {
			if (value != null && !value.isBlank()) {
				builder.append("- ").append(value).append(System.lineSeparator());
			}
		}

		if (builder.isEmpty()) {
			return "Not provided by model.";
		}

		return builder.toString();
	}
}