package com.antonstrokov.jaide.plugin.language;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JaideLanguageResolverTest {
	private final JaideLanguageResolver resolver = new JaideLanguageResolver();

	@Test
	void prefersKnownPsiLanguageOverFileExtension() {
		assertEquals("java", resolver.resolve("JAVA", "Example.kt"));
	}

	@Test
	void fallsBackToFileExtensionWhenPsiLanguageIsUnavailable() {
		assertEquals("kotlin", resolver.resolve(null, "Example.kt"));
		assertEquals("sql", resolver.resolve("TEXT", "query.sql"));
	}
}
