package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.dto.JaideExplanation;
import com.antonstrokov.jaide.plugin.state.JaideResultState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

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
		return """
            J-Aide Explanation
            ==================

            Summary
            -------
            %s

            Details
            -------
            %s

            Complexity
            ----------
            %s

            Suggestion
            ----------
            %s

            Best Practice
            -------------
            %s

            Risk Hint
            ---------
            %s

            Confidence
            ----------
            %s

            Code Smell
            ----------
            %s
            """.formatted(
				safeText(explanation.summary()),
				safeText(explanation.details()),
				safeText(explanation.complexity()),
				safeText(explanation.suggestion()),
				safeText(explanation.bestPractice()),
				safeText(explanation.riskHint()),
				safeText(explanation.confidence()),
				safeText(explanation.codeSmell())
		);
	}

	private static String safeText(String value) {
		return value == null || value.isBlank() ? "Not provided" : value;
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

		toolWindow.getComponent().add(panel);
	}
}