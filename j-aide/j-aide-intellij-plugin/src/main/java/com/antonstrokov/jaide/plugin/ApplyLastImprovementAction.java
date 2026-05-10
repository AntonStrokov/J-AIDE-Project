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

		WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
			Document document = editor.getDocument();

			document.replaceString(
					improvement.selectionStart(),
					improvement.selectionEnd(),
					improvement.improvedCode()
			);
		});

		JaideImprovementState.clear();

		notificationService.showInfo(e.getProject(), "Improvement applied. Use Ctrl+Z to undo.");
	}
}