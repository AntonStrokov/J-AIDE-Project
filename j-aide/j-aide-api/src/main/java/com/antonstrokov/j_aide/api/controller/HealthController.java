package com.antonstrokov.j_aide.api.controller;

import com.antonstrokov.j_aide.core.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	private final HealthService healthService;

	public HealthController(HealthService healthService) {
		this.healthService = healthService;
	}

	@GetMapping("/ping")
	public String ping() {
		return healthService.getStatus();
	}

	@GetMapping("/explain")
	public String explain() {
		return healthService.explainStatus();
	}
}