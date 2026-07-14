package com.antonstrokov.jaide.plugin.config;

public final class JaideUiLabels {

	// Button labels
	public static final String SHOW_DIFF_BUTTON = "Show Diff";
	public static final String APPLY_BUTTON = "Apply";
	public static final String BACK_TO_CODE_BUTTON = "Back to Code";
	public static final String START_WITH_CODE_BUTTON =  "Start with Code";
	public static final String APPLY_IMPROVEMENT_BUTTON = "Apply Improvement";
	public static final String COPY_IMPROVED_CODE_BUTTON = "Copy Code";
	public static final String CLOSE_BUTTON = "Close";
	public static final String CHECK_AI_SETUP_BUTTON = "Check AI Setup";
	public static final String RETRY_BUTTON = "Retry";

	// Preview section labels
	public static final String EMPTY_STATE_TITLE =
			"J-Aide Assistant";

	public static final String EMPTY_STATE_INTRO =
			"Start by selecting code in the editor or choosing one of the J-Aide actions.";

	public static final String EMPTY_STATE_CODE_ACTIONS_SECTION =
			"Code Actions";

	public static final String EMPTY_STATE_EXPLAIN_ACTION =
			"J-Aide: Explain Selected Code";

	public static final String EMPTY_STATE_IMPROVE_ACTION =
			"J-Aide: Improve Selected Code";

	public static final String EMPTY_STATE_GENERATE_TESTS_ACTION =
			"J-Aide: Generate Tests";

	public static final String EMPTY_STATE_ERROR_ANALYSIS_SECTION =
			"Error Analysis";

	public static final String EMPTY_STATE_ERROR_HINT =
			"Select or copy a stack trace and run:";

	public static final String EMPTY_STATE_EXPLAIN_ERROR_ACTION =
			"J-Aide: Explain Runtime Error";

	public static final String EMPTY_STATE_AI_SETUP_SECTION =
			"AI Setup";

	public static final String EMPTY_STATE_AI_SETUP_HINT =
			"Use Check AI Setup to verify the backend, provider and configured model.";

	public static final String EMPTY_STATE_NOTE_SECTION =
			"Note";

	public static final String EMPTY_STATE_RESTART_NOTE =
			"Previous AI results are not restored after an IDE restart.";

	public static final String SUMMARY_SECTION = "Summary";
	public static final String DETAILS_SECTION = "Details";
	public static final String ORIGINAL_CODE_SECTION = "Original Code";
	public static final String IMPROVED_CODE_SECTION = "Improved Code";
	public static final String CHANGES_SECTION = "Changes";
	public static final String RISK_HINT_SECTION = "Risk Hint";
	public static final String CONFIDENCE_SECTION = "Confidence";
	public static final String LIKELY_CAUSE_SECTION = "Likely Cause";
	public static final String WHERE_TO_LOOK_SECTION = "Where To Look";
	public static final String SUGGESTED_FIXES_SECTION = "Suggested Fixes";
	public static final String RUNTIME_ERROR_EXPLANATION_TITLE = "J-Aide Runtime Error Explanation";
	public static final String NOT_PROVIDED = "Not provided";
	public static final String NOT_PROVIDED_BY_MODEL = "Not provided by model.";
	public static final String EXPLANATION_TITLE = "J-Aide Explanation";
	public static final String COMPLEXITY_SECTION = "Complexity";
	public static final String SUGGESTION_SECTION = "Suggestion";
	public static final String BEST_PRACTICE_SECTION = "Best Practice";
	public static final String CODE_SMELL_SECTION = "Code Smell";
	public static final String IMPROVE_PREVIEW_TITLE = "J-Aide Improve Preview";
	public static final String STATUS_SECTION = "Status";
	public static final String IMPROVE_PREVIEW_STATUS =
			"This is a preview only. No files were changed.";
	public static final String TEST_GENERATION_PREVIEW_TITLE = "J-Aide Test Generation Preview";
	public static final String TEST_GENERATION_PREVIEW_STATUS =
			"This is a preview only. No test files were created.";
	public static final String GENERATED_TEST_CODE_SECTION = "Generated Test Code";
	public static final String TEST_FRAMEWORK_SECTION = "Test Framework";
	public static final String COVERED_SCENARIOS_SECTION = "Covered Scenarios";

	public static final String AI_HEALTH_PREVIEW_TITLE =
			"J-Aide AI Setup Status";
	public static final String BACKEND_STATUS_SECTION = "Backend";
	public static final String PROVIDER_STATUS_SECTION = "Provider";
	public static final String MODEL_STATUS_SECTION = "Model";
	public static final String PROVIDER_VERSION_SECTION = "Provider Version";
	public static final String RESPONSE_TIME_SECTION = "Response Time";
	public static final String HEALTH_MESSAGE_SECTION = "Message";
	public static final String AI_HEALTH_LOADING_TITLE =
			"Checking AI Setup";
	public static final String AI_HEALTH_LOADING_MESSAGE =
			"Checking backend, provider and configured model...";
	public static final String AI_HEALTH_ERROR_TITLE =
			"AI Setup Check Failed";
	public static final String AI_HEALTH_ERROR_MESSAGE_SECTION =
			"Error";

	private JaideUiLabels() {
	}
}
