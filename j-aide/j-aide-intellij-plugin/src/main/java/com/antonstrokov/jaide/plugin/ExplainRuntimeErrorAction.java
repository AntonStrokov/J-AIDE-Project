package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.client.JaideBackendClient;
import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainRequest;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplanation;
import com.antonstrokov.jaide.plugin.error.JaideErrorMessageBuilder;
import com.antonstrokov.jaide.plugin.factory.error.JaideErrorExplainRequestFactory;
import com.antonstrokov.jaide.plugin.model.JaideRuntimeErrorInput;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.service.JaideRuntimeErrorInputExtractor;
import com.antonstrokov.jaide.plugin.service.JaideRuntimeErrorInputValidationService;
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
	private final JaideErrorMessageBuilder errorMessageBuilder = new JaideErrorMessageBuilder();
	private final JaideNotificationService notificationService = new JaideNotificationService();
	private final JaideToolWindowService toolWindowService = new JaideToolWindowService();
	private final JaideRuntimeErrorInputValidationService inputValidationService =
			new JaideRuntimeErrorInputValidationService();
	private final JaideRuntimeErrorInputExtractor inputExtractor = new JaideRuntimeErrorInputExtractor();
	private final JaideErrorExplainRequestFactory requestFactory = new JaideErrorExplainRequestFactory();

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		log.info("Explain runtime error action started");

		JaideRuntimeErrorInput errorInput = inputExtractor.extract(e);

		if (errorInput == null) {
			log.warn("Explain runtime error action stopped: no selected error text and clipboard is empty");
			notificationService.showWarning(
					e.getProject(),
					"Please select error text or copy stack trace to clipboard first"
			);
			return;
		}

		if (!inputValidationService.looksLikeErrorText(errorInput.errorText())) {
			log.warn("Explain runtime error action stopped: input does not look like error text, source="
					+ errorInput.source()
					+ ", textLength=" + errorInput.errorText().length());

			notificationService.showWarning(
					e.getProject(),
					"Selected text does not look like an error, stack trace, or log. Use J-Aide: Explain Selected " +
							"Code" +
							" for source code."
			);
			return;
		}

		log.info("Runtime error input extracted, source=" + errorInput.source()
				+ ", fileName=" + errorInput.fileName()
				+ ", errorTextLength=" + errorInput.errorText().length()
				+ ", lineStart=" + errorInput.lineStart()
				+ ", lineEnd=" + errorInput.lineEnd());

		new Task.Backgroundable(e.getProject(), JaideConstants.EXPLAIN_ERROR_TASK_TITLE, false) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					log.info("Creating explain runtime error request");

					JaideErrorExplainRequest request = requestFactory.create(errorInput);

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
}