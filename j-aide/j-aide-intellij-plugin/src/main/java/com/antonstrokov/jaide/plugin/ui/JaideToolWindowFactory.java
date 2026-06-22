package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.config.JaideToolWindowMode;
import com.antonstrokov.jaide.plugin.config.JaideUiLabels;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplanation;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplanation;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.antonstrokov.jaide.plugin.service.JaideCopyImprovedCodeService;
import com.antonstrokov.jaide.plugin.service.JaideToolWindowActionsService;
import com.antonstrokov.jaide.plugin.ui.error.JaideErrorExplanationPreviewPanel;
import com.antonstrokov.jaide.plugin.ui.explain.JaideExplanationPreviewPanel;
import com.antonstrokov.jaide.plugin.ui.improve.JaideImprovePreviewPanel;
import com.antonstrokov.jaide.plugin.ui.settings.JaideExplainModeSelectorPanel;
import com.antonstrokov.jaide.plugin.dto.tests.JaideTestGenerationResult;
import com.antonstrokov.jaide.plugin.ui.tests.JaideTestGenerationPreviewPanel;
import com.antonstrokov.jaide.plugin.service.JaideCopyGeneratedTestCodeService;
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
	private static final JaideCopyImprovedCodeService copyImprovedCodeService = new JaideCopyImprovedCodeService();
	private static final JaideCopyGeneratedTestCodeService copyGeneratedTestCodeService =
			new JaideCopyGeneratedTestCodeService();
	private static final JaideToolWindowAutoHideService autoHideService = new JaideToolWindowAutoHideService();
	private static final JaideToolWindowActionsService toolWindowActionsService = new JaideToolWindowActionsService();
	private static JPanel previewContainer;
	private static JaideImprovePreviewPanel improvePreviewPanel;
	private static JaideExplanationPreviewPanel explanationPreviewPanel;
	private static JaideErrorExplanationPreviewPanel errorExplanationPreviewPanel;
	private static JaideTestGenerationPreviewPanel testGenerationPreviewPanel;
	private static JPanel actionsPanel;
	private static JaideExplainModeSelectorPanel explainModeSelectorPanel;
	private static JButton showDiffButton;
	private static JButton applyButton;
	private static JButton copyCodeButton;
	private static JButton backToCodeButton;
	private static JaideToolWindowMode currentMode;

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

			if (explainModeSelectorPanel != null) {
				explainModeSelectorPanel.setVisible(true);
			}

			if (showDiffButton != null) {
				showDiffButton.setVisible(false);
			}

			if (applyButton != null) {
				applyButton.setVisible(false);
			}

			if (copyCodeButton != null) {
				copyCodeButton.setVisible(false);
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

			if (explainModeSelectorPanel != null) {
				explainModeSelectorPanel.setVisible(false);
			}

			if (showDiffButton != null) {
				showDiffButton.setVisible(false);
			}

			if (applyButton != null) {
				applyButton.setVisible(false);
			}

			if (copyCodeButton != null) {
				copyCodeButton.setVisible(false);
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

			if (explainModeSelectorPanel != null) {
				explainModeSelectorPanel.setVisible(false);
			}

			if (showDiffButton != null) {
				showDiffButton.setVisible(true);
			}

			if (applyButton != null) {
				applyButton.setVisible(true);
			}

			if (copyCodeButton != null) {
				copyCodeButton.setVisible(true);
			}

			if (backToCodeButton != null) {
				backToCodeButton.setVisible(true);
			}
		});
	}

	public static void updateTestGeneration(JaideTestGenerationResult testGenerationResult) {
		ApplicationManager.getApplication().invokeLater(() -> {
			currentMode = JaideToolWindowMode.TEST_GENERATION;

			if (previewContainer != null && testGenerationPreviewPanel != null) {
				previewContainer.removeAll();
				testGenerationPreviewPanel.updateTestGeneration(testGenerationResult);
				previewContainer.add(testGenerationPreviewPanel, BorderLayout.CENTER);
				previewContainer.revalidate();
				previewContainer.repaint();
			}

			if (actionsPanel != null) {
				actionsPanel.setVisible(true);
			}

			if (explainModeSelectorPanel != null) {
				explainModeSelectorPanel.setVisible(false);
			}

			if (showDiffButton != null) {
				showDiffButton.setVisible(false);
			}

			if (applyButton != null) {
				applyButton.setVisible(false);
			}

			if (copyCodeButton != null) {
				copyCodeButton.setVisible(true);
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
		explainModeSelectorPanel = new JaideExplainModeSelectorPanel();
		explainModeSelectorPanel.setVisible(false);
		panel.add(explainModeSelectorPanel, BorderLayout.NORTH);

		actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		actionsPanel.setVisible(false);

		showDiffButton = new JButton(JaideUiLabels.SHOW_DIFF_BUTTON);
		showDiffButton.addActionListener(
				event -> toolWindowActionsService.showLatestImprovementDiff(project)
		);

		applyButton = new JButton(JaideUiLabels.APPLY_BUTTON);
		applyButton.addActionListener(
				event -> toolWindowActionsService.applyLatestImprovement(project)
		);

		copyCodeButton = new JButton(JaideUiLabels.COPY_IMPROVED_CODE_BUTTON);
		copyCodeButton.addActionListener(event -> {
			if (currentMode == JaideToolWindowMode.TEST_GENERATION) {
				copyGeneratedTestCodeService.copyLatestGeneratedTestCode(project);
				return;
			}

			copyImprovedCodeService.copyLatestImprovedCode(project);
		});

		backToCodeButton = new JButton(JaideUiLabels.BACK_TO_CODE_BUTTON);
		backToCodeButton.addActionListener(
				event -> toolWindowActionsService.backToCode(project)
		);

		actionsPanel.add(showDiffButton);
		actionsPanel.add(applyButton);
		actionsPanel.add(copyCodeButton);
		actionsPanel.add(backToCodeButton);
		panel.add(actionsPanel, BorderLayout.SOUTH);

		previewContainer = new JPanel(new BorderLayout());

		improvePreviewPanel = new JaideImprovePreviewPanel(project, "");
		explanationPreviewPanel = new JaideExplanationPreviewPanel("");
		errorExplanationPreviewPanel = new JaideErrorExplanationPreviewPanel();
		testGenerationPreviewPanel = new JaideTestGenerationPreviewPanel(project);

		previewContainer.add(improvePreviewPanel, BorderLayout.CENTER);
		panel.add(previewContainer, BorderLayout.CENTER);

		Content content = ContentFactory.getInstance().createContent(panel, "", false);
		toolWindow.getContentManager().addContent(content);

		autoHideService.register(project, toolWindow);
	}
}
