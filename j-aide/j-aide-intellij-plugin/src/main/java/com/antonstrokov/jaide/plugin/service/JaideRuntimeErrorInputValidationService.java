package com.antonstrokov.jaide.plugin.service;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JaideRuntimeErrorInputValidationService {
	private static final int MAX_VALIDATION_TEXT_LENGTH = 32768;

	private static final String[] JVM_ERROR_MARKERS = {
			"\\bjava:\\s+",
			"\\bat\\s+[a-zA-Z0-9_$]+\\.",
			"caused by:",
			"exception\\b",
			"throwable\\b",
			"cannot find symbol",
			"cannot resolve symbol",
			"class, interface, enum, or record expected",
			"beancreationexception",
			"unsatisfieddependencyexception",
			"failed to start",
			"incompatible types",
			"preview feature",
			"disabled by default",
			"enable-preview",
			"source option",
			"target option",
			"release version",
			"package does not exist",
			"method does not override",
			"illegal character",
			"illegal unicode escape",
			"symbol:",
			"location:"
	};

	private static final String[] JAVASCRIPT_ERROR_MARKERS = {
			"\\bat\\s+Async\\s+",
			"\\bnew\\s+Promise\\s+\\(<anonymous>\\)",
			"uncaught\\s+typeerror:",
			"\\bjavascript\\s+error"
	};

	private static final String[] SQL_ERROR_MARKERS = {
			"sqlexception",
			"syntax error in sql",
			"foreign key constraint",
			"table not found",
			"column not found",
			"unique constraint violation"
	};

	private static final String[] XML_CONFIG_ERROR_MARKERS = {
			"xml\\s+parse\\s+error",
			"markup\\s+not\\s+allowed",
			"the\\s+element\\s+type\\s+\"[^\"]+\"\\s+must\\s+be\\s+terminated",
			"unmarshalexception"
	};

	private static final String[] SYSTEM_ERROR_MARKERS = {
			"stack trace",
			"trace:",
			"connection refused",
			"port already in use",
			"error response from daemon",
			"ports are not available"
	};

	private static final Pattern ERROR_PATTERN = buildErrorPattern();

	private static Pattern buildErrorPattern() {
		String markers = Arrays.stream(new String[][]{
						JVM_ERROR_MARKERS,
						JAVASCRIPT_ERROR_MARKERS,
						SQL_ERROR_MARKERS,
						XML_CONFIG_ERROR_MARKERS,
						SYSTEM_ERROR_MARKERS
				})
				.flatMap(Arrays::stream)
				.collect(Collectors.joining("|"));

		return Pattern.compile(markers, Pattern.CASE_INSENSITIVE);
	}

	public boolean looksLikeErrorText(String text) {
		if (text == null || text.isBlank()) {
			return false;
		}

		// Scan only the first 32 KB to avoid expensive validation on very large logs.
		String textToCheck = text.length() > MAX_VALIDATION_TEXT_LENGTH
				? text.substring(0, MAX_VALIDATION_TEXT_LENGTH)
				: text;

		return ERROR_PATTERN.matcher(textToCheck).find();
	}
}
