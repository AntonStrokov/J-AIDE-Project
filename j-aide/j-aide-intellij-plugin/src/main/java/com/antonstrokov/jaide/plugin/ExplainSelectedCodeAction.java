package com.antonstrokov.jaide.plugin;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class ExplainSelectedCodeAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		NotificationGroupManager.getInstance()
				.getNotificationGroup("J-Aide Notifications")
				.createNotification("J-Aide action works!", NotificationType.INFORMATION)
				.notify(e.getProject());
	}
}