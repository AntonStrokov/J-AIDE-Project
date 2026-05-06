package com.antonstrokov.jaide.plugin.notification;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.antonstrokov.jaide.plugin.config.JaideConstants;

public class JaideNotificationService {
		public void showWarning(Project project, String message) {
		show(project, message, NotificationType.WARNING);
	}

	public void showError(Project project, String message) {
		show(project, message, NotificationType.ERROR);
	}

	private void show(Project project, String message, NotificationType type) {
		NotificationGroupManager.getInstance()
				.getNotificationGroup(JaideConstants.NOTIFICATION_GROUP)
				.createNotification(message, type)
				.notify(project);
	}
}