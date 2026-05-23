package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.dto.explain.JaideExplanation;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.service.JaideApplyImprovementService;
import com.antonstrokov.jaide.plugin.service.JaideDiffViewerService;
import com.antonstrokov.jaide.plugin.state.JaideImprovementState;
import com.antonstrokov.jaide.plugin.state.JaideLastImprovement;
import com.antonstrokov.jaide.plugin.ui.explain.JaideExplanationPreviewPanel;
import com.antonstrokov.jaide.plugin.ui.improve.JaideImprovePreviewPanel;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplanation;
import com.antonstrokov.jaide.plugin.ui.error.JaideErrorExplanationPreviewPanel;
import com.antonstrokov.jaide.plugin.config.JaideToolWindowMode;
import com.antonstrokov.jaide.plugin.ui.settings.JaideExplainModeSelectorPanel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class JaideToolWindowFactory implements ToolWindowFactory {
	private static final JaideDiffViewerService diffViewerService = new JaideDiffViewerService();
	private static final JaideApplyImprovementService applyImprovementService = new JaideApplyImprovementService();
	private static final JaideNotificationService notificationService = new JaideNotificationService();
	private static JPanel previewContainer;
	private static JaideImprovePreviewPanel improvePreviewPanel;
	private static JaideExplanationPreviewPanel explanationPreviewPanel;
	private static JaideErrorExplanationPreviewPanel errorExplanationPreviewPanel;
	private static JPanel actionsPanel;
	private static JButton showDiffButton;
	private static JButton applyButton;
	private static JButton backToCodeButton;
	private static JaideToolWindowMode currentMode;
	private static final JaideToolWindowAutoHideService autoHideService = new JaideToolWindowAutoHideService();

	public static void updateExplanation(JaideExplanation explanation) {
		ApplicationManager.getApplication().invokeLater(() -> {
			currentMode = JaideToolWindowMode.EXPLANATION;

			if (previewContainer != null && explanationPreviewPanel != null) {
				previewContainer.removeAll();
				explanationPreviewPanel.updateExplanation(explanation);
				previewContainer.add(explanationPreviewPanel, BorderLayout.CENTER);
				previewContainer.revalidate();
				previewContainer.repaint();
			}

			if (actionsPanel != null) {
				actionsPanel.setVisible(true);
			}

			if (showDiffButton != null) {
				showDiffButton.setVisible(false);
			}

			if (applyButton != null) {
				applyButton.setVisible(false);
			}

			if (backToCodeButton != null) {
				backToCodeButton.setVisible(true);
			}
		});
	}

	public static void updateErrorExplanation(JaideErrorExplanation errorExplanation) {
		ApplicationManager.getApplication().invokeLater(() -> {
			currentMode = JaideToolWindowMode.ERROR_EXPLANATION;

			if (previewContainer != null && errorExplanationPreviewPanel != null) {
				previewContainer.removeAll();
				errorExplanationPreviewPanel.updateErrorExplanation(errorExplanation);
				previewContainer.add(errorExplanationPreviewPanel, BorderLayout.CENTER);
				previewContainer.revalidate();
				previewContainer.repaint();
			}

			if (actionsPanel != null) {
				actionsPanel.setVisible(true);
			}

			if (showDiffButton != null) {
				showDiffButton.setVisible(false);
			}

			if (applyButton != null) {
				applyButton.setVisible(false);
			}

			if (backToCodeButton != null) {
				backToCodeButton.setVisible(true);
			}
		});
	}

	public static void updateImprovement(JaideImprovement improvement, String originalCode) {
		ApplicationManager.getApplication().invokeLater(() -> {
			currentMode = JaideToolWindowMode.IMPROVEMENT;

			if (previewContainer != null && improvePreviewPanel != null) {
				previewContainer.removeAll();
				improvePreviewPanel.updateImprovement(improvement, originalCode);
				previewContainer.add(improvePreviewPanel, BorderLayout.CENTER);
				previewContainer.revalidate();
				previewContainer.repaint();
			}

			if (actionsPanel != null) {
				actionsPanel.setVisible(true);
			}

			if (showDiffButton != null) {
				showDiffButton.setVisible(true);
			}

			if (applyButton != null) {
				applyButton.setVisible(true);
			}

			if (backToCodeButton != null) {
				backToCodeButton.setVisible(true);
			}
		});
	}

	public static boolean isShowingErrorExplanation() {
		return currentMode == JaideToolWindowMode.ERROR_EXPLANATION;
	}

	@Override
	public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		panel.add(new JaideExplainModeSelectorPanel(), BorderLayout.NORTH);

		actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		actionsPanel.setVisible(false);

		showDiffButton = new JButton("Show Diff");
		showDiffButton.addActionListener(event -> {
			JaideLastImprovement latestImprovement = JaideImprovementState.getLatestImprovement();

			if (latestImprovement == null) {
				notificationService.showWarning(project, "No improvement to show in diff yet");
				return;
			}

			if (latestImprovement.originalCode() == null || latestImprovement.improvedCode() == null) {
				notificationService.showWarning(project, "Cannot show diff: improvement data is incomplete");
				return;
			}

			new JaideToolWindowService().hide(project);

			diffViewerService.showImproveDiff(
					project,
					latestImprovement.originalCode(),
					latestImprovement.improvedCode(),
					latestImprovement.fileName()
			);
		});

		applyButton = new JButton("Apply");
		applyButton.addActionListener(event -> {
			applyImprovementService.applyLatestImprovement(project);

			new JaideToolWindowService().hide(project);
		});

		backToCodeButton = new JButton("Back to Code");
		backToCodeButton.addActionListener(event -> new JaideToolWindowService().hide(project));

		actionsPanel.add(showDiffButton);
		actionsPanel.add(applyButton);
		actionsPanel.add(backToCodeButton);
		panel.add(actionsPanel, BorderLayout.SOUTH);

		previewContainer = new JPanel(new BorderLayout());

		improvePreviewPanel = new JaideImprovePreviewPanel(project, "");
		explanationPreviewPanel = new JaideExplanationPreviewPanel("");
		errorExplanationPreviewPanel = new JaideErrorExplanationPreviewPanel();

		previewContainer.add(improvePreviewPanel, BorderLayout.CENTER);
		panel.add(previewContainer, BorderLayout.CENTER);

		Content content = ContentFactory.getInstance().createContent(panel, "", false);
		toolWindow.getContentManager().addContent(content);

		autoHideService.register(project, toolWindow);
	}
}
