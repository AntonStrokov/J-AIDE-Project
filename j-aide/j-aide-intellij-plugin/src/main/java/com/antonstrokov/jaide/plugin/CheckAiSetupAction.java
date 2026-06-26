package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.service.JaideAiSetupCheckService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class CheckAiSetupAction extends AnAction {
	private final JaideAiSetupCheckService aiSetupCheckService =
			new JaideAiSetupCheckService();

	@Override
	public void actionPerformed(@NotNull AnActionEvent event) {
		aiSetupCheckService.check(event.getProject());
	}
}