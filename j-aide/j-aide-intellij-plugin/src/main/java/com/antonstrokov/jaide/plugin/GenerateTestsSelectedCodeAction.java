package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.client.JaideBackendClient;
import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.config.JaideNotificationMessages;
import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.context.JaideEditorContextExtractor;
import com.antonstrokov.jaide.plugin.dto.tests.JaideTestGenerationRequest;
import com.antonstrokov.jaide.plugin.dto.tests.JaideTestGenerationResult;
import com.antonstrokov.jaide.plugin.error.JaideErrorMessageBuilder;
import com.antonstrokov.jaide.plugin.factory.tests.JaideTestGenerationRequestFactory;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowFactory;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowService;
import com.antonstrokov.jaide.plugin.state.JaideLastGeneratedTest;
import com.antonstrokov.jaide.plugin.state.JaideTestGenerationState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

public class GenerateTestsSelectedCodeAction extends AnAction {
	private static final Logger log = Logger.getInstance(GenerateTestsSelectedCodeAction.class);

	private final JaideBackendClient backendClient = new JaideBackendClient();
	private final JaideEditorContextExtractor contextExtractor = new JaideEditorContextExtractor();
	private final JaideErrorMessageBuilder errorMessageBuilder = new JaideErrorMessageBuilder();
	private final JaideNotificationService notificationService = new JaideNotificationService();
	private final JaideToolWindowService toolWindowService = new JaideToolWindowService();
	private final JaideTestGenerationRequestFactory requestFactory = new JaideTestGenerationRequestFactory();

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		log.info("Generate tests selected code action started");

		JaideEditorContext context = contextExtractor.extract(e);

		if (context == null) {
			log.warn("Generate tests action stopped: no selected code");
			notificationService.showWarning(e.getProject(), JaideNotificationMessages.SELECT_CODE_FIRST);
			return;
		}

		log.info("Generate tests context extracted, fileName=" + context.fileName()
				+ ", selectedCodeLength=" + context.selectedCode().length()
				+ ", lineStart=" + context.lineStart()
				+ ", lineEnd=" + context.lineEnd());

		new Task.Backgroundable(e.getProject(), JaideConstants.GENERATE_TESTS_TASK_TITLE, false) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					log.info("Creating test generation request");

					JaideTestGenerationRequest request = requestFactory.create(context);

					log.info("Sending test generation request");

					JaideTestGenerationResult result = backendClient.generateTests(request);

					JaideTestGenerationState.setLatestGeneratedTest(
							new JaideLastGeneratedTest(
									result.testCode(),
									context.fileName(),
									context.projectName(),
									context.moduleName(),
									context.lineStart(),
									context.lineEnd()
							)
					);

					log.info("Test generation response processed, testCodeLength="
							+ getLength(result.testCode()));

					toolWindowService.open(e.getProject());
					JaideToolWindowFactory.updateTestGeneration(result);

					notificationService.showInfo(
							e.getProject(),
							JaideNotificationMessages.TESTS_GENERATED_SUCCESSFULLY
					);

				} catch (Exception ex) {
					log.warn("Generate tests action failed: " + ex.getMessage(), ex);

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
