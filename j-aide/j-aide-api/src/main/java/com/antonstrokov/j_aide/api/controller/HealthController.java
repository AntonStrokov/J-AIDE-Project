package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.api.dto.*;
import com.antonstrokov.j_aide.core.config.AiProperties;
import com.antonstrokov.j_aide.core.config.AppProperties;
import com.antonstrokov.j_aide.core.dto.ExplainMode;
import com.antonstrokov.j_aide.core.dto.SupportedFeature;
import com.antonstrokov.j_aide.core.dto.SupportedLanguage;
import com.antonstrokov.j_aide.core.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController

public class HealthController {

	private final HealthService healthService;
	private final AppProperties appProperties;
	private final AiProperties aiProperties;

	public HealthController(HealthService healthService, AppProperties appProperties, AiProperties aiProperties) {
		this.healthService = healthService;
		this.appProperties = appProperties;
		this.aiProperties = aiProperties;
	}

	@GetMapping("/ping")
	public String ping() {
		return healthService.getStatus();
	}

	@GetMapping("/explain")
	public String explain() {
		return healthService.explainStatus();
	}

	@GetMapping("/backend-info")
	public BackendInfoResponse backendInfo() {
		BackendInfoResponse response = new BackendInfoResponse();

		BackendMetadata metadata = buildBackendMetadata();
		response.setMetadata(metadata);

		BackendDefaults defaults = buildBackendDefaults();
		response.setDefaults(defaults);

		BackendLlmInfo llmInfo = buildBackendLlmInfo();
		response.setLlmInfo(llmInfo);

		BackendEndpoints endpoints = buildBackendEndpoints();
		response.setEndpoints(endpoints);

		BackendCapabilities capabilities = buildBackendCapabilities();
		response.setCapabilities(capabilities);

		return response;
	}

	private BackendMetadata buildBackendMetadata() {
		BackendMetadata metadata = new BackendMetadata();
		metadata.setBackendName(appProperties.name());
		metadata.setBackendVersion(appProperties.version());
		metadata.setStatus("UP");

		return metadata;
	}

	private BackendDefaults buildBackendDefaults() {
		BackendDefaults defaults = new BackendDefaults();
		defaults.setDefaultMode(ExplainMode.SMART.name());
		defaults.setDefaultLanguage(SupportedLanguage.JAVA.name());

		return defaults;
	}

	private BackendLlmInfo buildBackendLlmInfo() {
		BackendLlmInfo llmInfo = new BackendLlmInfo();
		llmInfo.setLlmProvider("OLLAMA");
		llmInfo.setLlmModel(aiProperties.ollama().model());

		return llmInfo;
	}

	private BackendEndpoints buildBackendEndpoints() {
		BackendEndpoints endpoints = new BackendEndpoints();
		endpoints.setExplainEndpoint("/ai/explain");

		return endpoints;
	}

	private BackendCapabilities buildBackendCapabilities() {
		BackendCapabilities capabilities = new BackendCapabilities();

		capabilities.setSupportedModes(Arrays.stream(ExplainMode.values())
				.map(Enum::name)
				.toList());

		capabilities.setSupportedLanguages(Arrays.stream(SupportedLanguage.values())
				.map(Enum::name)
				.toList());

		capabilities.setSupportedFeatures(Arrays.stream(SupportedFeature.values())
				.map(Enum::name)
				.toList());

		return capabilities;
	}
}