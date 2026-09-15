package com.antonstrokov.jaide.plugin.config;

public final class JaideConstants {

	public static final String BACKEND_BASE_URL = "http://localhost:8080";

	public static final String EXPLAIN_URL = BACKEND_BASE_URL + "/ai/explain";
	public static final String IMPROVE_URL = BACKEND_BASE_URL + "/ai/improve";
	public static final String EXPLAIN_ERROR_URL = BACKEND_BASE_URL + "/ai/explain-error";
	public static final String TESTS_URL = BACKEND_BASE_URL + "/ai/tests";
	public static final String AI_HEALTH_URL = BACKEND_BASE_URL + "/ai/health";

	public static final String TOOL_WINDOW_ID = "J-Aide";
	public static final String NOTIFICATION_GROUP = "J-Aide Notifications";

	public static final String EXPLAIN_TASK_TITLE = "J-Aide: Explaining selected code";
	public static final String IMPROVE_TASK_TITLE = "J-Aide: Improving selected code";
	public static final String EXPLAIN_ERROR_TASK_TITLE = "J-Aide: Explaining runtime error";
	public static final String GENERATE_TESTS_TASK_TITLE = "J-Aide: Generating tests";
	public static final String CHECK_AI_SETUP_TASK_TITLE = "J-Aide: Checking AI setup";

	public static final int MAX_RUNTIME_ERROR_VALIDATION_TEXT_LENGTH = 32768;

	private JaideConstants() {
	}
}
