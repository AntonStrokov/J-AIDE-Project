package com.antonstrokov.j_aide.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "j-aide.app")
public record AppProperties(
		String name,
		String version
) {
}