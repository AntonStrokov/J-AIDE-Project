package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.config.JaideNotificationMessages;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.state.JaideImprovementState;
import com.antonstrokov.jaide.plugin.state.JaideLastImprovement;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class JaideApplyImprovementService {
	private static final Logger log = Logger.getInstance(JaideApplyImprovementService.class);

	private final JaideNotificationService notificationService = new JaideNotificationService();

	public void applyLatestImprovement(Project project) {
		log.info("Apply latest improvement started");

		if (!JaideImprovementState.hasLatestImprovement()) {
			log.warn("Apply stopped: no latest improvement found");
			notificationService.showWarning(project, JaideNotificationMessages.NO_IMPROVEMENT_TO_APPLY);
			return;
		}

		if (project == null) {
			log.warn("Apply stopped: project is null");
			notificationService.showWarning(null, JaideNotificationMessages.NO_ACTIVE_PROJECT_FOUND);
			return;
		}

		JaideLastImprovement improvement = JaideImprovementState.getLatestImprovement();
		Document document = improvement.document();

		log.info("Latest improvement loaded, fileName=" + improvement.fileName()
				+ ", projectName=" + improvement.projectName()
				+ ", moduleName=" + improvement.moduleName()
				+ ", originalCodeLength=" + getLength(improvement.originalCode())
				+ ", improvedCodeLength=" + getLength(improvement.improvedCode())
				+ ", selectionStart=" + improvement.selectionStart()
				+ ", selectionEnd=" + improvement.selectionEnd());

		if (document == null) {
			log.warn("Apply stopped: original document is null");
			notificationService.showWarning(project, JaideNotificationMessages.ORIGINAL_DOCUMENT_UNAVAILABLE);
			return;
		}

		if (!isValidSelectionRange(document, improvement)) {
			log.warn("Apply stopped: invalid selection range, documentLength=" + document.getTextLength()
					+ ", selectionStart=" + improvement.selectionStart()
					+ ", selectionEnd=" + improvement.selectionEnd());

			notificationService.showWarning(
					project,
					JaideNotificationMessages.ORIGINAL_SELECTION_RANGE_INVALID
			);
			return;
		}

		if (!isOriginalCodeStillPresent(document, improvement)) {
			log.warn("Apply stopped: original selected code has changed since improvement was generated");

			notificationService.showWarning(
					project,
					JaideNotificationMessages.SELECTED_CODE_CHANGED_SINCE_IMPROVEMENT
			);
			return;
		}

		log.info("Apply safety checks passed, replacing document text");

		WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(
				improvement.selectionStart(),
				improvement.selectionEnd(),
				improvement.improvedCode()
		));

		log.info("Document text replaced, opening original file");

		openOriginalFile(project, document);

		JaideImprovementState.clear();

		log.info("Latest improvement state cleared");

		notificationService.showInfo(
				project,
				JaideNotificationMessages.IMPROVEMENT_APPLIED
		);

		log.info("Apply latest improvement finished successfully");
	}

	private boolean isValidSelectionRange(Document document, JaideLastImprovement improvement) {
		int selectionStart = improvement.selectionStart();
		int selectionEnd = improvement.selectionEnd();
		int documentLength = document.getTextLength();

		return selectionStart >= 0
				&& selectionEnd >= selectionStart
				&& selectionEnd <= documentLength;
	}

	private boolean isOriginalCodeStillPresent(Document document, JaideLastImprovement improvement) {
		String currentText = document.getText().substring(
				improvement.selectionStart(),
				improvement.selectionEnd()
		);

		return currentText.equals(improvement.originalCode());
	}

	private void openOriginalFile(Project project, Document document) {
		VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);

		if (virtualFile == null) {
			return;
		}

		FileEditorManager.getInstance(project).openFile(
				virtualFile,
				true,
				true
		);
	}

	private int getLength(String value) {
		return value == null ? 0 : value.length();
	}
}