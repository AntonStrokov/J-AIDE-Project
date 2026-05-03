package com.antonstrokov.jaide.plugin.state;

public class JaideResultState {

	private static String latestSummary = "Select code and run J-Aide: Explain Selected Code";

	public static String getLatestSummary() {
		return latestSummary;
	}

	public static void setLatestSummary(String latestSummary) {
		JaideResultState.latestSummary = latestSummary;
	}
}