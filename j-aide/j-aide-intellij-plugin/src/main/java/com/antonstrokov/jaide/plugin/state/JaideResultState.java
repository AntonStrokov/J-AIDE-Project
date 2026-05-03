package com.antonstrokov.jaide.plugin.state;

public class JaideResultState {

	private static String latestSummary = """
        J-Aide Assistant

        Select code in the editor, right-click, and choose:
        J-Aide: Explain Selected Code

        The AI explanation will appear here.
        """;

	public static String getLatestSummary() {
		return latestSummary;
	}

	public static void setLatestSummary(String latestSummary) {
		JaideResultState.latestSummary = latestSummary;
	}
}