package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.state.JaideImprovementState;
import com.antonstrokov.jaide.plugin.state.JaideLastImprovement;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

public class ApplyLastImprovementAction extends AnAction {

	private final JaideNotificationService notificationService = new JaideNotificationService();

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		if (!JaideImprovementState.hasLatestImprovement()) {
			notificationService.showWarning(e.getProject(), "No improvement to apply yet");
			return;
		}

		Editor editor = e.getData(CommonDataKeys.EDITOR);

		if (editor == null || e.getProject() == null) {
			notificationService.showWarning(e.getProject(), "No active editor found");
			return;
		}

		JaideLastImprovement improvement = JaideImprovementState.getLatestImprovement();

		Document document = editor.getDocument();

		if (!isValidSelectionRange(document, improvement)) {
			notificationService.showWarning(
					e.getProject(),
					"Cannot apply improvement: original selection range is no longer valid"
			);
			return;
		}

		if (!isOriginalCodeStillPresent(document, improvement)) {
			notificationService.showWarning(
					e.getProject(),
					"Cannot apply improvement: selected code has changed since improvement was generated"
			);
			return;
		}

		WriteCommandAction.runWriteCommandAction(e.getProject(), () -> document.replaceString(
				improvement.selectionStart(),
				improvement.selectionEnd(),
				improvement.improvedCode()
		));

		JaideImprovementState.clear();

		notificationService.showInfo(
				e.getProject(),
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