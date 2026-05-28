package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.config.JaideNotificationMessages;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.state.JaideLastGeneratedTest;
import com.antonstrokov.jaide.plugin.state.JaideTestGenerationState;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;

import java.awt.datatransfer.StringSelection;

public class JaideCopyGeneratedTestCodeService {
	private final JaideNotificationService notificationService = new JaideNotificationService();

	public void copyLatestGeneratedTestCode(Project project) {
		JaideLastGeneratedTest latestGeneratedTest = JaideTestGenerationState.getLatestGeneratedTest();

		if (latestGeneratedTest == null) {
			notificationService.showWarning(project, JaideNotificationMessages.NO_GENERATED_TEST_TO_COPY);
			return;
		}

		if (latestGeneratedTest.testCode() == null || latestGeneratedTest.testCode().isBlank()) {
			notificationService.showWarning(project, JaideNotificationMessages.INCOMPLETE_GENERATED_TEST_COPY_DATA);
			return;
		}

		CopyPasteManager.getInstance().setContents(
				new StringSelection(latestGeneratedTest.testCode())
		);

		notificationService.showShortInfo(project, JaideNotificationMessages.GENERATED_TEST_CODE_COPIED);
	}
}
