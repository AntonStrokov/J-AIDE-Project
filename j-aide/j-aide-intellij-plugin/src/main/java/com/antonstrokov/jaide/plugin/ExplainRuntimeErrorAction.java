package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.client.JaideBackendClient;
import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.context.JaideEditorContextExtractor;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainRequest;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplanation;
import com.antonstrokov.jaide.plugin.error.JaideErrorMessageBuilder;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowFactory;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

public class ExplainRuntimeErrorAction extends AnAction {
	private static final Logger log = Logger.getInstance(ExplainRuntimeErrorAction.class);

	private final JaideBackendClient backendClient = new JaideBackendClient();
	private final JaideEditorContextExtractor contextExtractor = new JaideEditorContextExtractor();
	private final JaideErrorMessageBuilder errorMessageBuilder = new JaideErrorMessageBuilder();
	private final JaideNotificationService notificationService = new JaideNotificationService();
	private final JaideToolWindowService toolWindowService = new JaideToolWindowService();

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		log.info("Explain runtime error action started");

		JaideEditorContext context = contextExtractor.extract(e);

		if (context == null) {
			log.warn("Explain runtime error action stopped: no selected error text");
			notificationService.showWarning(e.getProject(), "Please select error text first");
			return;
		}

		log.info("Runtime error context extracted, fileName=" + context.fileName()
				+ ", errorTextLength=" + context.selectedCode().length()
				+ ", lineStart=" + context.lineStart()
				+ ", lineEnd=" + context.lineEnd());

		new Task.Backgroundable(e.getProject(), JaideConstants.EXPLAIN_ERROR_TASK_TITLE, false) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					log.info("Creating explain runtime error request");

					JaideErrorExplainRequest request = createRequest(context);

					log.info("Sending explain runtime error request");

					JaideErrorExplanation errorExplanation = backendClient.explainError(request);

					log.info("Explain runtime error response processed, updating tool window");

					toolWindowService.open(e.getProject());
					JaideToolWindowFactory.updateErrorExplanation(errorExplanation);

					log.info("Explain runtime error tool window updated");

				} catch (Exception ex) {
					log.warn("Explain runtime error action failed: " + ex.getMessage(), ex);

					notificationService.showError(
							e.getProject(),
							errorMessageBuilder.build(ex)
					);
				}
			}
		}.queue();
	}

	private JaideErrorExplainRequest createRequest(JaideEditorContext context) {
		return new JaideErrorExplainRequest(
				context.selectedCode(),
				"runtime_error",
				null,
				context.fileName(),
				context.lineStart(),
				context.lineEnd(),
				context.projectName(),
				null,
				context.ideVersion(),
				JaideConstants.PLUGIN_VERSION,
				context.moduleName()
		);
	}
}