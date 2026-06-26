package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.client.JaideBackendClient;
import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthResponse;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthStatus;
import com.antonstrokov.jaide.plugin.error.JaideErrorMessageBuilder;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class JaideAiSetupCheckService {

	private static final Logger log =
			Logger.getInstance(JaideAiSetupCheckService.class);

	private final JaideBackendClient backendClient =
			new JaideBackendClient();

	private final JaideErrorMessageBuilder errorMessageBuilder =
			new JaideErrorMessageBuilder();

	private final JaideNotificationService notificationService =
			new JaideNotificationService();

	public void check(Project project) {
		check(
				project,
				response -> showHealthResult(project, response)
		);
	}

	public void check(
			Project project,
			Consumer<JaideHealthResponse> resultConsumer
	) {
		new Task.Backgroundable(
				project,
				JaideConstants.CHECK_AI_SETUP_TASK_TITLE,
				false
		) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					JaideHealthResponse response =
							backendClient.checkAiHealth();

					log.info(
							"AI setup check completed, backendStatus="
									+ response.backendStatus()
									+ ", providerStatus="
									+ response.providerStatus()
									+ ", modelStatus="
									+ response.modelStatus()
									+ ", responseTimeMs="
									+ response.responseTimeMs()
					);

					resultConsumer.accept(response);
				} catch (Exception exception) {
					log.warn(
							"AI setup check failed: "
									+ exception.getMessage(),
							exception
					);

					notificationService.showError(
							project,
							errorMessageBuilder.build(exception)
					);
				}
			}
		}.queue();
	}

	private void showHealthResult(
			Project project,
			JaideHealthResponse response
	) {
		if (hasStatus(response, JaideHealthStatus.FAILED)) {
			notificationService.showError(project, response.message());
			return;
		}

		if (hasStatus(response, JaideHealthStatus.DEGRADED)
				|| hasStatus(response, JaideHealthStatus.UNKNOWN)) {
			notificationService.showWarning(project, response.message());
			return;
		}

		notificationService.showInfo(project, response.message());
	}

	private boolean hasStatus(
			JaideHealthResponse response,
			JaideHealthStatus status
	) {
		return response.backendStatus() == status
				|| response.providerStatus() == status
				|| response.modelStatus() == status;
	}
}
