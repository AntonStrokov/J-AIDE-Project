package com.antonstrokov.jaide.plugin.ui;

import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

public class JaideToolWindowAutoHideService {

	private final JaideToolWindowService toolWindowService = new JaideToolWindowService();

	public void register(Project project, ToolWindow toolWindow) {
		if (project == null || toolWindow == null) {
			return;
		}

		MessageBusConnection connection = project.getMessageBus().connect(toolWindow.getDisposable());

		connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
			@Override
			public void selectionChanged(@NotNull FileEditorManagerEvent event) {
				if (JaideToolWindowFactory.isShowingErrorExplanation(project)) {
					toolWindowService.hide(project);
				}
			}
		});
	}
}
