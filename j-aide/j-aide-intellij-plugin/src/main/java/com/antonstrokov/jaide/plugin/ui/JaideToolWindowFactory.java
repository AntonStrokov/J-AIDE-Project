package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.dto.JaideExplanation;
import com.antonstrokov.jaide.plugin.state.JaideResultState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.awt.*;

public class JaideToolWindowFactory implements ToolWindowFactory {

	private static JTextArea resultTextArea;

	public static void updateExplanation(JaideExplanation explanation) {
		String formattedResult = formatExplanation(explanation);

		JaideResultState.setLatestSummary(formattedResult);

		ApplicationManager.getApplication().invokeLater(() -> {
			if (resultTextArea != null) {
				resultTextArea.setText(formattedResult);
				resultTextArea.setCaretPosition(0);
			}
		});
	}

	private static String formatExplanation(JaideExplanation explanation) {
		StringBuilder result = new StringBuilder();

		result.append("""
            J-Aide Explanation
            ==================
            
            """);

		appendSection(result, "Summary", explanation.summary());
		appendSection(result, "Details", explanation.details());
		appendSection(result, "Complexity", explanation.complexity());
		appendSection(result, "Suggestion", explanation.suggestion());
		appendSection(result, "Best Practice", explanation.bestPractice());
		appendSection(result, "Risk Hint", explanation.riskHint());
		appendSection(result, "Confidence", explanation.confidence());
		appendSection(result, "Code Smell", explanation.codeSmell());

		return result.toString();
	}


	@Override
	public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		resultTextArea = new JTextArea(JaideResultState.getLatestSummary());
		resultTextArea.setEditable(false);
		resultTextArea.setLineWrap(true);
		resultTextArea.setWrapStyleWord(true);

		JBScrollPane scrollPane = new JBScrollPane(resultTextArea);
		panel.add(scrollPane, BorderLayout.CENTER);

		Content content = ContentFactory.getInstance().createContent(panel, "", false);
		toolWindow.getContentManager().addContent(content);
	}

	private static void appendSection(StringBuilder result, String title, String value) {
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
}