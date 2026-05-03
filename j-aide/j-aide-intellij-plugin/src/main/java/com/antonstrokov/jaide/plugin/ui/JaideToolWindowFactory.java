package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.state.JaideResultState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;

public class JaideToolWindowFactory implements ToolWindowFactory {

	private static JTextArea resultTextArea;

	@Override
	public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		resultTextArea = new JTextArea(JaideResultState.getLatestSummary());
		resultTextArea.setEditable(false);
		resultTextArea.setLineWrap(true);
		resultTextArea.setWrapStyleWord(true);

		panel.add(resultTextArea, BorderLayout.CENTER);

		toolWindow.getComponent().add(panel);
	}

	public static void updateSummary(String summary) {
		JaideResultState.setLatestSummary(summary);

		ApplicationManager.getApplication().invokeLater(() -> {
			if (resultTextArea != null) {
				resultTextArea.setText(summary);
				resultTextArea.setCaretPosition(0);
			}
		});
	}
}