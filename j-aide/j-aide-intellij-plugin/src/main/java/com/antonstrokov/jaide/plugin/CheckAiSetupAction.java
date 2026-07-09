package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.service.JaideAiSetupCheckService;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class CheckAiSetupAction extends AnAction {
	private final JaideAiSetupCheckService aiSetupCheckService =
			new JaideAiSetupCheckService();
	private final JaideToolWindowService toolWindowService =
			new JaideToolWindowService();

	@Override
	public void actionPerformed(@NotNull AnActionEvent event) {
		toolWindowService.open(event.getProject());
		aiSetupCheckService.check(event.getProject());
	}
}
