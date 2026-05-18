package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.client.JaideBackendClient;
import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.context.JaideEditorContextExtractor;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImproveRequest;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.antonstrokov.jaide.plugin.error.JaideErrorMessageBuilder;
import com.antonstrokov.jaide.plugin.factory.improve.JaideImproveRequestFactory;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.state.JaideImprovementState;
import com.antonstrokov.jaide.plugin.state.JaideLastImprovement;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowFactory;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.diagnostic.Logger;
import com.antonstrokov.jaide.plugin.service.JaideImprovementValidationService;
import org.jetbrains.annotations.NotNull;

public class ImproveSelectedCodeAction extends AnAction {
	private static final Logger log = Logger.getInstance(ImproveSelectedCodeAction.class);

	private final JaideBackendClient backendClient = new JaideBackendClient();
	private final JaideEditorContextExtractor contextExtractor = new JaideEditorContextExtractor();
	private final JaideErrorMessageBuilder errorMessageBuilder = new JaideErrorMessageBuilder();
	private final JaideNotificationService notificationService = new JaideNotificationService();
	private final JaideToolWindowService toolWindowService = new JaideToolWindowService();
	private final JaideImproveRequestFactory requestFactory = new JaideImproveRequestFactory();
	private final JaideImprovementValidationService validationService = new JaideImprovementValidationService();

	@Override
	public void actionPerformed(AnActionEvent e) {
		log.info("Improve selected code action started");

		JaideEditorContext context = contextExtractor.extract(e);

		if (context == null) {
			log.warn("Improve action stopped: no selected code");
			notificationService.showWarning(e.getProject(), "Please select code first");
			return;
		}

		log.info("Improve context extracted, fileName=" + context.fileName()
				+ ", selectedCodeLength=" + context.selectedCode().length()
				+ ", lineStart=" + context.lineStart()
				+ ", lineEnd=" + context.lineEnd());

		new Task.Backgroundable(e.getProject(), JaideConstants.IMPROVE_TASK_TITLE, false) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					log.info("Creating improve request");

					JaideImproveRequest request = requestFactory.create(context);

					log.info("Sending improve request");

					JaideImprovement improvement = backendClient.improve(request);

					log.info("Improve response processed, improvedCodeLength="
							+ getLength(improvement.improvedCode()));

					if (validationService.isBlankImprovedCode(improvement.improvedCode())) {
						log.warn("Improve action stopped: blank improved code detected");

						notificationService.showWarning(
								e.getProject(),
								"J-Aide received an empty improvement. Please try again."
						);

						return;
					}

					if (validationService.isNoOpImprovement(context.selectedCode(), improvement.improvedCode())) {
						log.warn("Improve action stopped: no-op improvement detected, selectedCodeLength="
								+ context.selectedCode().length()
								+ ", improvedCodeLength=" + getLength(improvement.improvedCode()));

						notificationService.showWarning(
								e.getProject(),
								"J-Aide did not find meaningful code changes. Please try again or select a different code fragment."
						);

						return;
					}

					if (validationService.hasMarkdownCodeFence(improvement.improvedCode())) {
						log.warn("Improve action stopped: markdown code fence detected in improved code, improvedCodeLength="
								+ getLength(improvement.improvedCode()));

						notificationService.showWarning(
								e.getProject(),
								"J-Aide received an invalid improvement format. Please try again."
						);

						return;
					}

					if (validationService.hasNoChangeDescriptions(improvement.changes())) {
						log.warn("Improve action stopped: missing change descriptions, improvedCodeLength="
								+ getLength(improvement.improvedCode()));

						notificationService.showWarning(
								e.getProject(),
								"J-Aide received an improvement without change descriptions. Please try again."
						);

						return;
					}

					log.info("Storing latest improvement");

					JaideImprovementState.setLatestImprovement(
							new JaideLastImprovement(
									context.selectedCode(),
									improvement.improvedCode(),
									context.fileName(),
									context.projectName(),
									context.moduleName(),
									context.lineStart(),
									context.lineEnd(),
									context.selectionStart(),
									context.selectionEnd(),
									context.document()
							)
					);

					log.info("Latest improvement stored, updating tool window");

					toolWindowService.open(e.getProject());
					JaideToolWindowFactory.updateImprovement(improvement, context.selectedCode());

					log.info("Improve tool window updated");

				} catch (Exception ex) {
					log.warn("Improve action failed: " + ex.getMessage(), ex);

					notificationService.showError(
							e.getProject(),
							errorMessageBuilder.build(ex)
					);
				}
			}
		}.queue();
	}

	private int getLength(String value) {
		return value == null ? 0 : value.length();
	}
}