package com.antonstrokov.jaide.plugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.antonstrokov.jaide.plugin.config.JaideConstants;

public class JaideToolWindowService {

	public void open(Project project) {
		if (project == null) {
			return;
		}

		ApplicationManager.getApplication().invokeLater(() -> {
			ToolWindow toolWindow = ToolWindowManager
					.getInstance(project)
					.getToolWindow(JaideConstants.TOOL_WINDOW_ID);

			if (toolWindow != null) {
				toolWindow.activate(null);
			}
		});
	}
}