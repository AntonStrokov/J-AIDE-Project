package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.client.JaideBackendClient;
import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.context.JaideEditorContextExtractor;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplainRequest;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplanation;
import com.antonstrokov.jaide.plugin.error.JaideErrorMessageBuilder;
import com.antonstrokov.jaide.plugin.factory.explain.JaideExplainRequestFactory;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowFactory;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowService;
import com.antonstrokov.jaide.plugin.service.JaideRuntimeErrorInputValidationService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

public class ExplainSelectedCodeAction extends AnAction {
	private static final Logger log = Logger.getInstance(ExplainSelectedCodeAction.class);

	private final JaideBackendClient backendClient = new JaideBackendClient();
	private final JaideEditorContextExtractor contextExtractor = new JaideEditorContextExtractor();
	private final JaideErrorMessageBuilder errorMessageBuilder = new JaideErrorMessageBuilder();
	private final JaideNotificationService notificationService = new JaideNotificationService();
	private final JaideToolWindowService toolWindowService = new JaideToolWindowService();
	private final JaideExplainRequestFactory requestFactory = new JaideExplainRequestFactory();
	private final JaideRuntimeErrorInputValidationService runtimeErrorInputValidationService =
			new JaideRuntimeErrorInputValidationService();

	@Override
	public void actionPerformed(AnActionEvent e) {
		log.info("Explain selected code action started");

		JaideEditorContext context = contextExtractor.extract(e);

		if (context == null) {
			log.warn("Explain action stopped: no selected code");
			notificationService.showWarning(e.getProject(), "Please select code first");
			return;
		}

		if (runtimeErrorInputValidationService.looksLikeErrorText(context.selectedCode())) {
			log.warn("Explain action stopped: selected text looks like runtime error text, selectedCodeLength="
					+ context.selectedCode().length());

			notificationService.showWarning(
					e.getProject(),
					"Selected text looks like an error, stack trace, or log. Use J-Aide: Explain Runtime Error instead."
			);
			return;
		}

		log.info("Explain context extracted, fileName=" + context.fileName()
				+ ", selectedCodeLength=" + context.selectedCode().length()
				+ ", lineStart=" + context.lineStart()
				+ ", lineEnd=" + context.lineEnd());

		new Task.Backgroundable(e.getProject(), JaideConstants.EXPLAIN_TASK_TITLE, false) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					log.info("Creating explain request");

					JaideExplainRequest request = requestFactory.create(context);

					log.info("Explain request created, mode=" + request.mode());
					log.info("Sending explain request");

					JaideExplanation explanation = backendClient.explain(request);

					log.info("Explain response processed, updating tool window");

					toolWindowService.open(e.getProject());
					JaideToolWindowFactory.updateExplanation(explanation);

					log.info("Explain tool window updated");

				} catch (Exception ex) {
					log.warn("Explain action failed: " + ex.getMessage(), ex);

					notificationService.showError(
							e.getProject(),
							errorMessageBuilder.build(ex)
					);
				}
			}
		}.queue();
	}
}