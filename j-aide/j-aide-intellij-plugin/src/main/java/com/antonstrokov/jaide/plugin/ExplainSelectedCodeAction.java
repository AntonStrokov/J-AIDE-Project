package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.context.JaideEditorContextExtractor;
import com.antonstrokov.jaide.plugin.dto.JaideExplainRequest;
import com.antonstrokov.jaide.plugin.dto.JaideExplanation;
import com.antonstrokov.jaide.plugin.error.JaideErrorMessageBuilder;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowFactory;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;
import com.antonstrokov.jaide.plugin.model.JaideExplainMode;


public class ExplainSelectedCodeAction extends AnAction {
	private final JaideBackendClient backendClient = new JaideBackendClient();
	private final JaideEditorContextExtractor contextExtractor = new JaideEditorContextExtractor();
	private final JaideErrorMessageBuilder errorMessageBuilder = new JaideErrorMessageBuilder();
	private final JaideNotificationService notificationService = new JaideNotificationService();
	private final JaideToolWindowService toolWindowService = new JaideToolWindowService();

	@Override
	public void actionPerformed(AnActionEvent e) {
		JaideEditorContext context = contextExtractor.extract(e);

		if (context == null) {
			notificationService.showWarning(e.getProject(), "Please select code first");
			return;
		}

		new Task.Backgroundable(e.getProject(), "J-Aide: Explaining selected code", false) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					JaideExplainRequest request = new JaideExplainRequest(
							context.selectedCode(),
							JaideExplainMode.SMART,
							context.fileName(),
							context.lineStart(),
							context.lineEnd(),
							context.projectName(),
							context.moduleName(),
							context.ideVersion()
					);

					JaideExplanation explanation = backendClient.explain(request);

					JaideToolWindowFactory.updateExplanation(explanation);
					toolWindowService.open(e.getProject());

				} catch (Exception ex) {
					notificationService.showError(
							e.getProject(),
							errorMessageBuilder.build(ex)
					);
				}
			}
		}.queue();
	}
}