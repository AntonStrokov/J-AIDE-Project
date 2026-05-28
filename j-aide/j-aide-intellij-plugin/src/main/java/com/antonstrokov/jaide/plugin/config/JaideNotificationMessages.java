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

	public static final String NO_IMPROVEMENT_TO_APPLY =
			"No improvement to apply yet";

	public static final String NO_ACTIVE_PROJECT_FOUND =
			"No active project found";

	public static final String ORIGINAL_DOCUMENT_UNAVAILABLE =
			"Cannot apply improvement: original document is no longer available";

	public static final String ORIGINAL_SELECTION_RANGE_INVALID =
			"Cannot apply improvement: original selection range is no longer valid";

	public static final String SELECTED_CODE_CHANGED_SINCE_IMPROVEMENT =
			"Cannot apply improvement: selected code has changed since improvement was generated";

	public static final String IMPROVEMENT_APPLIED =
			"Improvement applied. Use IDE Undo/Redo to revert or reapply.";

	public static final String NO_IMPROVEMENT_TO_SHOW_IN_DIFF =
			"No improvement to show in diff yet";

	public static final String INCOMPLETE_IMPROVEMENT_DIFF_DATA =
			"Cannot show diff: improvement data is incomplete";

	public static final String NO_IMPROVEMENT_TO_COPY =
			"No improvement to copy yet";

	public static final String INCOMPLETE_IMPROVEMENT_COPY_DATA =
			"Cannot copy improvement: improved code is not available";

	public static final String IMPROVED_CODE_COPIED =
			"Improved code copied to clipboard";

	public static final String NO_GENERATED_TEST_TO_COPY =
			"No generated test code to copy";

	public static final String INCOMPLETE_GENERATED_TEST_COPY_DATA =
			"Generated test copy data is incomplete";

	public static final String GENERATED_TEST_CODE_COPIED =
			"Generated test code copied to clipboard";

	public static final String TESTS_GENERATED_SUCCESSFULLY = "Tests generated successfully";

	private JaideNotificationMessages() {
	}
}
