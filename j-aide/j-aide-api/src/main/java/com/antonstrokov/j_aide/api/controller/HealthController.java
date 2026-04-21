package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.api.dto.BackendInfoResponse;
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
		return new BackendInfoResponse(
				appProperties.name(),
				appProperties.version(),
				ExplainMode.SMART.name(),
				SupportedLanguage.JAVA.name(),
				"OLLAMA",
				aiProperties.ollama().model(),
				"UP",
				"/ai/explain",
				Arrays.stream(ExplainMode.values())
						.map(Enum::name)
						.toList(),
				Arrays.stream(SupportedLanguage.values())
						.map(Enum::name)
						.toList(),
				Arrays.stream(SupportedFeature.values())
						.map(Enum::name)
						.toList()
		);
	}
}