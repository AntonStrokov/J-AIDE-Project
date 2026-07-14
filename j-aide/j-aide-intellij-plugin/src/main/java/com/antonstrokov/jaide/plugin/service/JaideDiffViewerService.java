package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.ui.JaideToolWindowService;
import com.antonstrokov.jaide.plugin.ui.diff.JaideDiffDialog;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

public class JaideDiffViewerService {
	private static final Logger log = Logger.getInstance(JaideDiffViewerService.class);

	private static final JaideApplyImprovementService applyImprovementService = new JaideApplyImprovementService();
	private static final JaideToolWindowService toolWindowService =
			new JaideToolWindowService();

	public void showImproveDiff(
			Project project,
			String originalCode,
			String improvedCode,
			String fileName
	) {
		log.info("Show improve diff started, fileName=" + fileName
				+ ", originalCodeLength=" + getLength(originalCode)
				+ ", improvedCodeLength=" + getLength(improvedCode));

		if (project == null) {
			log.warn("Show improve diff stopped: project is null");
			return;
		}

		if (originalCode == null) {
			log.warn("Show improve diff stopped: original code is null");
			return;
		}

		if (improvedCode == null) {
			log.warn("Show improve diff stopped: improved code is null");
			return;
		}

		ApplicationManager.getApplication().invokeLater(() -> {
			log.info("Creating improve diff request on EDT");

			DiffContentFactory contentFactory = DiffContentFactory.getInstance();

			DiffContent originalContent = contentFactory.create(project, originalCode);
			DiffContent improvedContent = contentFactory.create(project, improvedCode);

			SimpleDiffRequest request = new SimpleDiffRequest(
					buildTitle(fileName),
					originalContent,
					improvedContent,
					"Original",
					"Improved"
			);

			log.info("Opening improve diff dialog, title=" + buildTitle(fileName));

			JaideDiffDialog dialog = new JaideDiffDialog(
					project,
					request,
					applyImprovementService
			);

			dialog.show();

			if (dialog.getExitCode() == DialogWrapper.CANCEL_EXIT_CODE) {
				toolWindowService.open(project);
			}

			log.info("Improve diff dialog opened");
		});
	}

	private String buildTitle(String fileName) {
		if (fileName == null || fileName.isBlank()) {
			return "J-Aide Improve Diff";
		}

		return "J-Aide Improve Diff: " + fileName;
	}

	private int getLength(String value) {
		return value == null ? 0 : value.length();
	}
}
