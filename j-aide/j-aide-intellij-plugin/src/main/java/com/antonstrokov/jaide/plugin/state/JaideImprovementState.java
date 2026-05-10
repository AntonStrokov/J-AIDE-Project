package com.antonstrokov.jaide.plugin.state;

public class JaideImprovementState {

	private static JaideLastImprovement latestImprovement;

	public static JaideLastImprovement getLatestImprovement() {
		return latestImprovement;
	}

	public static void setLatestImprovement(JaideLastImprovement latestImprovement) {
		JaideImprovementState.latestImprovement = latestImprovement;
	}

	public static boolean hasLatestImprovement() {
		return latestImprovement != null;
	}

	public static void clear() {
		latestImprovement = null;
	}
}