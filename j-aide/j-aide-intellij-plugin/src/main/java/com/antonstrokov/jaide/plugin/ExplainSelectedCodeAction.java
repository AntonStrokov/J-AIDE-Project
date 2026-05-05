package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.context.JaideEditorContextExtractor;
import com.antonstrokov.jaide.plugin.dto.JaideExplanation;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowFactory;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import com.antonstrokov.jaide.plugin.dto.JaideExplainRequest;
import com.antonstrokov.jaide.plugin.error.JaideErrorMessageBuilder;


public class ExplainSelectedCodeAction extends AnAction {

	private final JaideBackendClient backendClient = new JaideBackendClient();
	private final JaideEditorContextExtractor contextExtractor = new JaideEditorContextExtractor();
	private final JaideErrorMessageBuilder errorMessageBuilder = new JaideErrorMessageBuilder();


	@Override
	public void actionPerformed(AnActionEvent e) {
		JaideEditorContext context = contextExtractor.extract(e);

		if (context == null) {
			showNotification(e, "Please select code first", NotificationType.WARNING);
			return;
		}

		new Task.Backgroundable(e.getProject(), "J-Aide: Explaining selected code", false) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					JaideExplainRequest request = new JaideExplainRequest(
							context.selectedCode(),
							context.fileName(),
							context.lineStart(),
							context.lineEnd(),
							context.projectName(),
							context.moduleName(),
							context.ideVersion()
					);

					JaideExplanation explanation = backendClient.explain(request);

					JaideToolWindowFactory.updateExplanation(explanation);
					openJaideToolWindow(e);

				} catch (Exception ex) {
					showNotification(
							e,
							errorMessageBuilder.build(ex),
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