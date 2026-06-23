package com.antonstrokov.j_aide.core.integration.ollama;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OllamaClient {
	private final RestClient restClient = RestClient.create();
}