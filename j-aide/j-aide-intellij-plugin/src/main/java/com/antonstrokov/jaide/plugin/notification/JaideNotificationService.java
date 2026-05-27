package com.antonstrokov.jaide.plugin.notification;

import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.AppExecutorUtil;

import java.util.concurrent.TimeUnit;

public class JaideNotificationService {
	private static final int INFO_EXPIRE_DELAY_MILLIS = 3000;
	private static final int WARNING_EXPIRE_DELAY_MILLIS = 8000;
	private static final int ERROR_EXPIRE_DELAY_MILLIS = 10000;

	public void showWarning(Project project, String message) {
		showExpiring(project, message, NotificationType.WARNING, WARNING_EXPIRE_DELAY_MILLIS);
	}

	public void showError(Project project, String message) {
		showExpiring(project, message, NotificationType.ERROR, ERROR_EXPIRE_DELAY_MILLIS);
	}

	public void showInfo(Project project, String message) {
		showShortInfo(project, message);
	}

	public void showShortInfo(Project project, String message) {
		showExpiring(project, message, NotificationType.INFORMATION, INFO_EXPIRE_DELAY_MILLIS);
	}

	private void showExpiring(Project project, String message, NotificationType type, int expireDelayMillis) {
		Notification notification = createNotification(message, type);
		notification.notify(project);

		AppExecutorUtil.getAppScheduledExecutorService().schedule(
				notification::expire,
				expireDelayMillis,
				TimeUnit.MILLISECONDS
		);
	}

	private Notification createNotification(String message, NotificationType type) {
		return NotificationGroupManager.getInstance()
				.getNotificationGroup(JaideConstants.NOTIFICATION_GROUP)
				.createNotification(message, type);
	}
}
