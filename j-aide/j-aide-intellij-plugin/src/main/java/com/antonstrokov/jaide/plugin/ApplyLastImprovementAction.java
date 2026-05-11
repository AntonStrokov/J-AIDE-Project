package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.service.JaideApplyImprovementService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

public class ApplyLastImprovementAction extends AnAction {

	private final JaideApplyImprovementService applyImprovementService = new JaideApplyImprovementService();

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		Editor editor = e.getData(CommonDataKeys.EDITOR);

		applyImprovementService.applyLatestImprovement(
				e.getProject(),
				editor
		);
	}
}