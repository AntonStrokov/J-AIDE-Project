package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.client.JaideBackendClient;
import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthResponse;
import com.antonstrokov.jaide.plugin.error.JaideErrorMessageBuilder;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class JaideAiSetupCheckService {

	private static final Logger log =
			Logger.getInstance(JaideAiSetupCheckService.class);

	private final JaideBackendClient backendClient =
			new JaideBackendClient();

	private final JaideErrorMessageBuilder errorMessageBuilder =
			new JaideErrorMessageBuilder();

	public void check(Project project) {
		check(
				project,
				response -> JaideToolWindowFactory.updateAiHealth(
						project,
						response,
						() -> check(project)
				),
				errorMessage -> JaideToolWindowFactory.updateAiHealthError(
						project,
						errorMessage,
						() -> check(project)
				)
		);
	}

	public void check(
			Project project,
			Consumer<JaideHealthResponse> resultConsumer,
			Consumer<String> errorConsumer
	) {
		JaideToolWindowFactory.updateAiHealthLoading(project);
		new Task.Backgroundable(
				project,
				JaideConstants.CHECK_AI_SETUP_TASK_TITLE,
				false
		) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				runCheck(
						backendClient::checkAiHealth,
						resultConsumer,
						errorConsumer
				);
			}
		}.queue();
	}

	void runCheck(
			Callable<JaideHealthResponse> healthCall,
			Consumer<JaideHealthResponse> resultConsumer,
			Consumer<String> errorConsumer
	) {
		JaideHealthResponse response;

		try {
			response = healthCall.call();
		} catch (Exception exception) {
			log.warn(
					"AI setup backend check failed: "
							+ exception.getMessage(),
					exception
			);

			String errorMessage = errorMessageBuilder.build(exception);
			errorConsumer.accept(errorMessage);
			return;
		}

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

		try {
			resultConsumer.accept(response);
		} catch (RuntimeException exception) {
			log.warn(
					"AI setup result update failed: "
							+ exception.getMessage(),
					exception
			);
			throw exception;
		}
	}
}
