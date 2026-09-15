package com.antonstrokov.jaide.plugin.state;

public class JaideTestGenerationState {

	private static JaideLastGeneratedTest latestGeneratedTest;

	public static JaideLastGeneratedTest getLatestGeneratedTest() {
		return latestGeneratedTest;
	}

	public static void setLatestGeneratedTest(JaideLastGeneratedTest latestGeneratedTest) {
		JaideTestGenerationState.latestGeneratedTest = latestGeneratedTest;
	}
}
