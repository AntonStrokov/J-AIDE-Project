package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.config.JaideNotificationMessages;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.state.JaideImprovementState;
import com.antonstrokov.jaide.plugin.state.JaideLastImprovement;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowService;
import com.intellij.openapi.project.Project;

public class JaideToolWindowActionsService {
	private final JaideDiffViewerService diffViewerService = new JaideDiffViewerService();
	private final JaideApplyImprovementService applyImprovementService = new JaideApplyImprovementService();
	private final JaideNotificationService notificationService = new JaideNotificationService();
	private final JaideToolWindowService toolWindowService = new JaideToolWindowService();

	public void showLatestImprovementDiff(Project project) {
		JaideLastImprovement latestImprovement = JaideImprovementState.getLatestImprovement();

		if (latestImprovement == null) {
			notificationService.showWarning(project, JaideNotificationMessages.NO_IMPROVEMENT_TO_SHOW_IN_DIFF);
			return;
		}

		if (latestImprovement.originalCode() == null || latestImprovement.improvedCode() == null) {
			notificationService.showWarning(project, JaideNotificationMessages.INCOMPLETE_IMPROVEMENT_DIFF_DATA);
			return;
		}

		toolWindowService.hide(project);

		diffViewerService.showImproveDiff(
				project,
				latestImprovement.originalCode(),
				latestImprovement.improvedCode(),
				latestImprovement.fileName()
		);
	}

	public void applyLatestImprovement(Project project) {
		applyImprovementService.applyLatestImprovement(project);
		toolWindowService.hide(project);
	}

	public void backToCode(Project project) {
		toolWindowService.hide(project);
	}
}
