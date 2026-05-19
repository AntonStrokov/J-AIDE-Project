package com.antonstrokov.jaide.plugin.config;

public final class JaideConstants {

	public static final String EXPLAIN_URL = "http://localhost:8080/ai/explain";
	public static final String IMPROVE_URL = "http://localhost:8080/ai/improve";
	public static final String EXPLAIN_ERROR_URL = "http://localhost:8080/ai/explain-error";

	public static final String PLUGIN_VERSION = "0.1.0";
	public static final String TOOL_WINDOW_ID = "J-Aide";
	public static final String NOTIFICATION_GROUP = "J-Aide Notifications";

	public static final String EXPLAIN_TASK_TITLE = "J-Aide: Explaining selected code";
	public static final String IMPROVE_TASK_TITLE = "J-Aide: Improving selected code";
	public static final String EXPLAIN_ERROR_TASK_TITLE = "J-Aide: Explaining runtime error";

	private JaideConstants() {
	}
}