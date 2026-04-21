package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.api.dto.BackendInfoResponse;
import com.antonstrokov.j_aide.core.config.AppProperties;
import com.antonstrokov.j_aide.core.dto.ExplainMode;
import com.antonstrokov.j_aide.core.dto.SupportedLanguage;
import com.antonstrokov.j_aide.core.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HealthController {

	private final HealthService healthService;
	private final AppProperties appProperties;

	public HealthController(HealthService healthService, AppProperties appProperties) {
		this.healthService = healthService;
		this.appProperties = appProperties;
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
				List.of(
						ExplainMode.FAST.name(),
						ExplainMode.SMART.name(),
						ExplainMode.DEEP.name()
				),
				List.of(
						SupportedLanguage.JAVA.name(),
						SupportedLanguage.KOTLIN.name(),
						SupportedLanguage.SQL.name(),
						SupportedLanguage.XML.name(),
						SupportedLanguage.JAVASCRIPT.name(),
						SupportedLanguage.PLAIN_TEXT.name()
				)
		);
	}
}