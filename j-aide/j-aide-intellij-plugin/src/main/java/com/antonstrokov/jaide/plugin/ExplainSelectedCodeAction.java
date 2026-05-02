package com.antonstrokov.jaide.plugin;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

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
					String summary = backendClient.explain(selectedText);

					showNotification(
							e,
							"J-Aide: " + summary,
							NotificationType.INFORMATION
					);
				} catch (Exception ex) {
					showNotification(e, "J-Aide backend error: " + ex.getMessage(), NotificationType.ERROR);
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
}