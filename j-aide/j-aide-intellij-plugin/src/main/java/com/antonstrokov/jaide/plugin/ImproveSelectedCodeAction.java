package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.context.JaideEditorContextExtractor;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImproveRequest;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.antonstrokov.jaide.plugin.error.JaideErrorMessageBuilder;
import com.antonstrokov.jaide.plugin.factory.improve.JaideImproveRequestFactory;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowFactory;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

public class ImproveSelectedCodeAction extends AnAction {
	private final JaideBackendClient backendClient = new JaideBackendClient();
	private final JaideEditorContextExtractor contextExtractor = new JaideEditorContextExtractor();
	private final JaideErrorMessageBuilder errorMessageBuilder = new JaideErrorMessageBuilder();
	private final JaideNotificationService notificationService = new JaideNotificationService();
	private final JaideToolWindowService toolWindowService = new JaideToolWindowService();
	private final JaideImproveRequestFactory requestFactory = new JaideImproveRequestFactory();

	@Override
	public void actionPerformed(AnActionEvent e) {
		JaideEditorContext context = contextExtractor.extract(e);

		if (context == null) {
			notificationService.showWarning(e.getProject(), "Please select code first");
			return;
		}

		new Task.Backgroundable(e.getProject(), JaideConstants.IMPROVE_TASK_TITLE, false) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					JaideImproveRequest request = requestFactory.create(context);

					JaideImprovement improvement = backendClient.improve(request);

					JaideToolWindowFactory.updateImprovement(improvement, context.selectedCode());
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