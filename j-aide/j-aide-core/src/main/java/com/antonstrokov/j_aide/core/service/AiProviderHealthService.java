package com.antonstrokov.j_aide.core.service;

import com.antonstrokov.j_aide.core.config.AiProperties;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthDiagnosticCode;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthResult;
import com.antonstrokov.j_aide.core.dto.health.AiProviderHealthStatus;
import com.antonstrokov.j_aide.core.integration.ollama.OllamaClient;
import com.antonstrokov.j_aide.core.integration.ollama.dto.OllamaGenerateResponse;
import com.antonstrokov.j_aide.core.integration.ollama.dto.OllamaTagsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class AiProviderHealthService {
	private static final String MODEL_TRIAL_PROMPT = "Reply with OK.";

	private final OllamaClient ollamaClient;
	private final AiProperties aiProperties;

	public AiProviderHealthService(
			OllamaClient ollamaClient,
			AiProperties aiProperties
	) {
		this.ollamaClient = ollamaClient;
		this.aiProperties = aiProperties;
	}

	public AiProviderHealthResult getHealthInfo() {
		return checkHealth(false);
	}

	public AiProviderHealthResult getSetupHealthInfo() {
		return checkHealth(true);
	}

	private AiProviderHealthResult checkHealth(boolean runTrialGeneration) {
		long startedAt = System.currentTimeMillis();
		String configuredModel = aiProperties.ollama().model();

		try {
			var versionResponse = ollamaClient.getVersion();
			OllamaTagsResponse tagsResponse = ollamaClient.getModels();

			String providerVersion = versionResponse == null
					? null
					: versionResponse.version();

			if (!hasValidModelList(tagsResponse)) {
				return new AiProviderHealthResult(
						AiProviderHealthStatus.READY,
						AiProviderHealthStatus.READY,
						AiProviderHealthStatus.UNKNOWN,
						providerVersion,
						System.currentTimeMillis() - startedAt,
						"AI provider returned an invalid model list.",
						AiProviderHealthDiagnosticCode.INVALID_PROVIDER_RESPONSE,
						configuredModel
				);
			}

			boolean configuredModelAvailable =
					isConfiguredModelAvailable(tagsResponse);

			boolean configuredModelReady = configuredModelAvailable
					&& (!runTrialGeneration || isConfiguredModelReady());

			String message;
			AiProviderHealthDiagnosticCode diagnosticCode;

			if (!configuredModelAvailable) {
				message = "Configured AI model is not available: "
						+ configuredModel;
				diagnosticCode = AiProviderHealthDiagnosticCode.MODEL_NOT_FOUND;
			} else if (!configuredModelReady) {
				message = "Configured AI model did not pass trial generation: "
						+ configuredModel;
				diagnosticCode = AiProviderHealthDiagnosticCode.MODEL_TRIAL_FAILED;
			} else {
				message = runTrialGeneration
						? "AI provider and configured model are ready."
						: "AI provider and configured model are available.";
				diagnosticCode = AiProviderHealthDiagnosticCode.NONE;
			}

			return new AiProviderHealthResult(
					AiProviderHealthStatus.READY,
					AiProviderHealthStatus.READY,
					configuredModelReady
							? AiProviderHealthStatus.READY
							: AiProviderHealthStatus.FAILED,
					providerVersion,
					System.currentTimeMillis() - startedAt,
					message,
					diagnosticCode,
					configuredModel
			);
		} catch (RestClientException ex) {
			return new AiProviderHealthResult(
					AiProviderHealthStatus.READY,
					AiProviderHealthStatus.FAILED,
					AiProviderHealthStatus.UNKNOWN,
					null,
					System.currentTimeMillis() - startedAt,
					"AI provider is not reachable: "
							+ aiProperties.ollama().baseUrl(),
					AiProviderHealthDiagnosticCode.PROVIDER_UNREACHABLE,
					configuredModel
			);
		}
	}

	private boolean isConfiguredModelAvailable(OllamaTagsResponse tagsResponse) {
		if (tagsResponse == null || tagsResponse.models() == null) {
			return false;
		}

		String configuredModel = aiProperties.ollama().model();

		return tagsResponse.models().stream()
				.anyMatch(modelInfo -> modelInfo != null
						&& (configuredModel.equals(modelInfo.name())
						|| configuredModel.equals(modelInfo.model())));
	}

	private boolean isSuccessfulTrialResponse(OllamaGenerateResponse response) {
		return response != null
				&& Boolean.TRUE.equals(response.done())
				&& response.response() != null
				&& !response.response().isBlank();
	}

	private boolean isConfiguredModelReady() {
		try {
			OllamaGenerateResponse response =
					ollamaClient.generate(MODEL_TRIAL_PROMPT);

			return isSuccessfulTrialResponse(response);
		} catch (RestClientException exception) {
			return false;
		}
	}

	private boolean hasValidModelList(OllamaTagsResponse response) {
		return response != null
				&& response.models() != null
				&& response.models().stream().allMatch(modelInfo ->
				modelInfo != null
						&& (hasText(modelInfo.name())
						|| hasText(modelInfo.model()))
		);
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}
}
