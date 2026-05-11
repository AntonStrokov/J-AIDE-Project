package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.state.JaideImprovementState;
import com.antonstrokov.jaide.plugin.state.JaideLastImprovement;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

public class JaideApplyImprovementService {

	private final JaideNotificationService notificationService = new JaideNotificationService();

	public void applyLatestImprovement(Project project, Editor editor) {
		if (!JaideImprovementState.hasLatestImprovement()) {
			notificationService.showWarning(project, "No improvement to apply yet");
			return;
		}

		if (editor == null || project == null) {
			notificationService.showWarning(project, "No active editor found");
			return;
		}

		JaideLastImprovement improvement = JaideImprovementState.getLatestImprovement();

		Document document = editor.getDocument();

		if (!isValidSelectionRange(document, improvement)) {
			notificationService.showWarning(
					project,
					"Cannot apply improvement: original selection range is no longer valid"
			);
			return;
		}

		if (!isOriginalCodeStillPresent(document, improvement)) {
			notificationService.showWarning(
					project,
					"Cannot apply improvement: selected code has changed since improvement was generated"
			);
			return;
		}

		WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(
				improvement.selectionStart(),
				improvement.selectionEnd(),
				improvement.improvedCode()
		));

		JaideImprovementState.clear();

		notificationService.showInfo(
				project,
				"Improvement applied. Use IDE Undo/Redo to revert or reapply."
		);
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
}