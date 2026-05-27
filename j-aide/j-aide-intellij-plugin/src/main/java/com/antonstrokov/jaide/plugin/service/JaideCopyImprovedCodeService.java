package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.config.JaideNotificationMessages;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.state.JaideImprovementState;
import com.antonstrokov.jaide.plugin.state.JaideLastImprovement;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;

import java.awt.datatransfer.StringSelection;

public class JaideCopyImprovedCodeService {
	private final JaideNotificationService notificationService = new JaideNotificationService();

	public void copyLatestImprovedCode(Project project) {
		JaideLastImprovement latestImprovement = JaideImprovementState.getLatestImprovement();

		if (latestImprovement == null) {
			notificationService.showWarning(project, JaideNotificationMessages.NO_IMPROVEMENT_TO_COPY);
			return;
		}

		if (latestImprovement.improvedCode() == null || latestImprovement.improvedCode().isBlank()) {
			notificationService.showWarning(project, JaideNotificationMessages.INCOMPLETE_IMPROVEMENT_COPY_DATA);
			return;
		}

		CopyPasteManager.getInstance().setContents(
				new StringSelection(latestImprovement.improvedCode())
		);

		notificationService.showInfo(project, JaideNotificationMessages.IMPROVED_CODE_COPIED);
	}
}
