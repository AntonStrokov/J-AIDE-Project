package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.config.JaideConstants;

import java.util.regex.Pattern;

public class JaideRuntimeErrorInputValidationService {

	private static final String[] STRUCTURED_ERROR_MARKERS = {
			// JVM stack traces
			"^\\s*exception in thread\\s+\"[^\"]+\"",
			"^\\s*(?:caused by|suppressed):\\s+[\\w.$]+(?:exception|error)(?::.*)?$",
			"^\\s*[\\w.$]+(?:exception|error):(?:\\s+.*)?$",
			"^\\s*at\\s+[\\w.$<>]+\\([^\\r\\n()]+:\\d+\\)\\s*$",

			// Java compiler errors
			"^.*\\.java:\\d+:\\s+(?:error|warning):",
			"^\\s*java:\\s+(?:cannot find symbol|cannot resolve symbol|incompatible types"
					+ "|package\\s+.+\\s+does not exist|method does not override"
					+ "|class, interface, enum, or record expected|illegal character"
					+ "|illegal unicode escape|preview feature|source option"
					+ "|target option|release version)",
			"^\\s*cannot resolve symbol\\s+['\"].+['\"]\\s*$",

			// Maven errors
			"^\\s*(?:\\[error]\\s+)?failed to execute goal\\s+.+$",
			"^\\s*(?:\\[error]\\s+)?non-parseable pom\\s+.+$",
			"^\\s*(?:\\[error]\\s+)?the build could not read\\s+.+$",
			"^\\s*(?:\\[error]\\s+)?could not resolve dependencies for project\\s+.+$",
			"^\\s*(?:\\[error]\\s+)?could not find artifact\\s+.+\\s+in\\s+.+$",

			// JavaScript and Node.js errors
			"^\\s*(?:uncaught\\s+)?(?:typeerror|referenceerror|syntaxerror):\\s+.+$",
			"^\\s*(?:error:\\s+)?cannot find module\\s+['\"].+['\"](?:\\s+.*)?$",
			"^\\s*code:\\s*['\"]module_not_found['\"]\\s*,?\\s*$",

			// SQL errors
			"^\\s*(?:java\\.sql\\.)?sqlexception:(?:\\s+.*)?$",
			"^\\s*sqlstate\\s*[:=]\\s*.+$",

			// XML and configuration errors
			"^\\s*xml\\s+parse\\s+error(?:\\s*[:.-].*)?$",
			"^.*the element type\\s+\"[^\"]+\"\\s+must be terminated.*$",
			"^.*end tag name.+must match start tag name.*$",
			"^.*markup\\s+not\\s+allowed.*$",

			// Application and system logs
			"^\\s*\\d{4}-\\d{2}-\\d{2}[ T].*\\b(?:error|fatal)\\b.+$",
			"^\\s*error response from daemon:\\s+.+$",
			"^\\s*bind for\\s+.+\\s+failed:\\s+port is already allocated\\s*$",
			"^\\s*ports are not available:\\s+.+$"
	};

	private static final Pattern STRUCTURED_ERROR_PATTERN = Pattern.compile(
			String.join("|", STRUCTURED_ERROR_MARKERS),
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
	);

	private static final Pattern CANNOT_FIND_SYMBOL_PATTERN = Pattern.compile(
			"^.*cannot find symbol.*$",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
	);

	private static final Pattern SYMBOL_DETAILS_PATTERN = Pattern.compile(
			"^\\s*symbol:\\s+.+$",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
	);

	private static final Pattern LOCATION_DETAILS_PATTERN = Pattern.compile(
			"^\\s*location:\\s+.+$",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
	);

	public boolean looksLikeErrorText(String text) {
		if (text == null || text.isBlank()) {
			return false;
		}

		// Scan only the first 32 KB to avoid expensive validation on very large logs.
		String textToCheck =
				text.length() > JaideConstants.MAX_RUNTIME_ERROR_VALIDATION_TEXT_LENGTH
						? text.substring(
						0,
						JaideConstants.MAX_RUNTIME_ERROR_VALIDATION_TEXT_LENGTH
				)
						: text;

		return STRUCTURED_ERROR_PATTERN.matcher(textToCheck).find()
				|| looksLikeCannotFindSymbolError(textToCheck);
	}

	private boolean looksLikeCannotFindSymbolError(String text) {
		return CANNOT_FIND_SYMBOL_PATTERN.matcher(text).find()
				&& SYMBOL_DETAILS_PATTERN.matcher(text).find()
				&& LOCATION_DETAILS_PATTERN.matcher(text).find();
	}
}
