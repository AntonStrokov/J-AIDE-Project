package com.antonstrokov.jaide.plugin.service;

import java.util.regex.Pattern;

public class JaideRuntimeErrorInputValidationService {

	private static final Pattern ERROR_PATTERN = Pattern.compile(
			// === 1. JAVA and KOTLIN (JVM stack traces and compiler errors) ===
			"\\bjava:\\s+|" +
					"\\bat\\s+[a-zA-Z0-9_$]+\\.|" +
					"caused by:|exception\\b|throwable\\b|" +
					"cannot find symbol|cannot resolve symbol|" +
					"class, interface, enum, or record expected|" +
					"beancreationexception|unsatisfieddependencyexception|failed to start|" +
					"incompatible types|" +
					"preview feature|" +
					"disabled by default|" +
					"enable-preview|" +
					"source option|" +
					"target option|" +
					"release version|" +
					"package does not exist|" +
					"method does not override|" +
					"illegal character|" +
					"illegal unicode escape|" +
					"symbol:|location:|" +

					// === 2. JAVASCRIPT / NODE.JS ===
					"\\bat\\s+Async\\s+|\\bnew\\s+Promise\\s+\\(<anonymous>\\)|" +
					"uncaught\\s+typeerror:|\\bjavascript\\s+error|" +

					// === 3. SQL (syntax and database errors) ===
					"sqlexception|syntax error in sql|foreign key constraint|" +
					"table not found|column not found|unique constraint violation|" +

					// === 4. XML / CONFIG (parsing, Maven, Spring errors) ===
					"xml\\s+parse\\s+error|markup\\s+not\\s+allowed|" +
					"the\\s+element\\s+type\\s+\"[^\"]+\"\\s+must\\s+be\\s+terminated|" +
					"unmarshalexception|" +

					// === 5. COMMON SYSTEM MARKERS (plain text / Docker logs) ===
					"stack trace|trace:|connection refused|port already in use",

			Pattern.CASE_INSENSITIVE
	);

	public boolean looksLikeErrorText(String text) {
		if (text == null || text.isBlank()) {
			return false;
		}

		// Scan only the first 32 KB to avoid expensive validation on very large logs.
		String textToCheck = text.length() > 32768 ? text.substring(0, 32768) : text;

		return ERROR_PATTERN.matcher(textToCheck).find();
	}
}