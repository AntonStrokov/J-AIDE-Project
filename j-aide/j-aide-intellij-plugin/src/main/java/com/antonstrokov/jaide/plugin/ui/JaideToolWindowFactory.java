package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.dto.explain.JaideExplanation;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.antonstrokov.jaide.plugin.state.JaideResultState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;

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

	public static void updateImprovement(JaideImprovement improvement) {
		String formattedResult = formatImprovement(improvement);

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

	private static String formatImprovement(JaideImprovement improvement) {
		StringBuilder result = new StringBuilder();

		result.append("""
            J-Aide Improvement
            ==================
            
            """);

		appendSection(result, "Summary", improvement.summary());
		appendSection(result, "Improved Code", improvement.improvedCode());
		appendChanges(result, improvement.changes());
		appendSection(result, "Risk Hint", improvement.riskHint());
		appendSection(result, "Confidence", improvement.confidence());

		return result.toString();
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

	private static void appendChanges(StringBuilder result, java.util.List<String> changes) {
		if (changes == null || changes.isEmpty()) {
			return;
		}

		result.append("Changes")
				.append(System.lineSeparator())
				.append("-------")
				.append(System.lineSeparator());

		for (String change : changes) {
			if (change != null && !change.isBlank()) {
				result.append("- ")
						.append(change)
						.append(System.lineSeparator());
			}
		}

		result.append(System.lineSeparator());
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
}