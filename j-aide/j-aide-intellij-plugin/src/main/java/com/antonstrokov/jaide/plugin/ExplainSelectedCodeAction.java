package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.dto.JaideExplanation;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowFactory;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.application.ApplicationManager;

public class ExplainSelectedCodeAction extends AnAction {

	private final JaideBackendClient backendClient = new JaideBackendClient();

	@Override
	public void actionPerformed(AnActionEvent e) {
		Editor editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR);

		if (editor == null) {
			showNotification(e, "No editor found", NotificationType.WARNING);
			return;
		}

		SelectionModel selectionModel = editor.getSelectionModel();
		String selectedText = selectionModel.getSelectedText();

		if (selectedText == null || selectedText.isBlank()) {
			showNotification(e, "Please select code first", NotificationType.WARNING);
			return;
		}

		new Task.Backgroundable(e.getProject(), "J-Aide: Explaining selected code", false) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					JaideExplanation explanation = backendClient.explain(selectedText);

					JaideToolWindowFactory.updateExplanation(explanation);
					openJaideToolWindow(e);

				} catch (Exception ex) {
					showNotification(
							e,
							buildErrorMessage(ex),
							NotificationType.ERROR
					);
				}
			}
		}.queue();
	}

	private void showNotification(AnActionEvent e, String message, NotificationType type) {
		NotificationGroupManager.getInstance()
				.getNotificationGroup("J-Aide Notifications")
				.createNotification(message, type)
				.notify(e.getProject());
	}

	private String buildErrorMessage(Exception ex) {
		String message = ex.getMessage();
		Throwable cause = ex.getCause();
		String causeMessage = cause == null ? null : cause.getMessage();

		if (containsConnectionRefused(message) || containsConnectionRefused(causeMessage)) {
			return "J-Aide backend is not available. Please start the backend on http://localhost:8080.";
		}

		if (message == null || message.isBlank()) {
			return "J-Aide backend error. Please check that the backend is running.";
		}

		return "J-Aide backend error: " + message;
	}

	private boolean containsConnectionRefused(String value) {
		return value != null && value.contains("Connection refused");
	}

	private void openJaideToolWindow(AnActionEvent e) {
		if (e.getProject() == null) {
			return;
		}

		ApplicationManager.getApplication().invokeLater(() -> {
			ToolWindow toolWindow = ToolWindowManager
					.getInstance(e.getProject())
					.getToolWindow("J-Aide");

			if (toolWindow != null) {
				toolWindow.activate(null);
			}
		});
	}
}