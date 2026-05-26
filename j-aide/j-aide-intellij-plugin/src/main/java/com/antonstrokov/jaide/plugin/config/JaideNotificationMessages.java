package com.antonstrokov.jaide.plugin.config;

public final class JaideNotificationMessages {

	public static final String SELECT_CODE_FIRST =
			"Please select code first";

	public static final String SELECT_ERROR_TEXT_OR_COPY_STACK_TRACE_FIRST =
			"Please select error text or copy stack trace to clipboard first";

	public static final String USE_EXPLAIN_RUNTIME_ERROR_FOR_ERROR_TEXT =
			"Selected text looks like an error, stack trace, or log. Use J-Aide: Explain Runtime Error instead.";

	public static final String USE_EXPLAIN_SELECTED_CODE_FOR_SOURCE_CODE =
			"Selected text does not look like an error, stack trace, or log. "
					+ "Use J-Aide: Explain Selected Code for source code.";

	public static final String EMPTY_IMPROVEMENT_RECEIVED =
			"J-Aide received an empty improvement. Please try again.";

	public static final String NO_MEANINGFUL_CODE_CHANGES =
			"J-Aide did not find meaningful code changes. Please try again or select a different code fragment.";

	public static final String INVALID_IMPROVEMENT_FORMAT =
			"J-Aide received an invalid improvement format. Please try again.";

	public static final String MISSING_IMPROVEMENT_CHANGE_DESCRIPTIONS =
			"J-Aide received an improvement without change descriptions. Please try again.";

	private JaideNotificationMessages() {
	}
}
