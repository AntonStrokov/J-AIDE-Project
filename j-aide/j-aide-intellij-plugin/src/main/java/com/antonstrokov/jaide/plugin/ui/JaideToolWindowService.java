package com.antonstrokov.jaide.plugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

public class JaideToolWindowService {

	public void open(Project project) {
		if (project == null) {
			return;
		}

		ApplicationManager.getApplication().invokeLater(() -> {
			ToolWindow toolWindow = ToolWindowManager
					.getInstance(project)
					.getToolWindow("J-Aide");

			if (toolWindow != null) {
				toolWindow.activate(null);
			}
		});
	}
}