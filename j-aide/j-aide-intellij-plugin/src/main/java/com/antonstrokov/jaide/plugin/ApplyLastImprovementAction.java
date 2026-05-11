package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.service.JaideApplyImprovementService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class ApplyLastImprovementAction extends AnAction {

	private final JaideApplyImprovementService applyImprovementService = new JaideApplyImprovementService();

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		applyImprovementService.applyLatestImprovement(e.getProject());
	}
}