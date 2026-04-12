package com.antonstrokov.j_aide.core.service;

import org.springframework.stereotype.Service;

@Service
public class HealthService {

	public String getStatus() {
		return "J-Aide core is alive";
	}

	public String explainStatus() {
		return "Explain feature will be here";
	}
}