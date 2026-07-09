package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.config.JaideUiLabels;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplanation;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplanation;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthResponse;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.antonstrokov.jaide.plugin.dto.tests.JaideTestGenerationResult;
import com.antonstrokov.jaide.plugin.service.JaideAiSetupCheckService;
import com.antonstrokov.jaide.plugin.service.JaideCopyGeneratedTestCodeService;
import com.antonstrokov.jaide.plugin.service.JaideCopyImprovedCodeService;
import com.antonstrokov.jaide.plugin.service.JaideToolWindowActionsService;
import com.antonstrokov.jaide.plugin.ui.error.JaideErrorExplanationPreviewPanel;
import com.antonstrokov.jaide.plugin.ui.explain.JaideExplanationPreviewPanel;
import com.antonstrokov.jaide.plugin.ui.health.JaideAiHealthPreviewPanel;
import com.antonstrokov.jaide.plugin.ui.improve.JaideImprovePreviewPanel;
import com.antonstrokov.jaide.plugin.ui.settings.JaideExplainModeSelectorPanel;
import com.antonstrokov.jaide.plugin.ui.tests.JaideTestGenerationPreviewPanel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class JaideToolWindowFactory implements ToolWindowFactory {
	private static final JaideCopyImprovedCodeService copyImprovedCodeService = new JaideCopyImprovedCodeService();
	private static final JaideCopyGeneratedTestCodeService copyGeneratedTestCodeService =
			new JaideCopyGeneratedTestCodeService();
	private static final JaideToolWindowAutoHideService autoHideService = new JaideToolWindowAutoHideService();
	private static final JaideToolWindowActionsService toolWindowActionsService = new JaideToolWindowActionsService();
	private static final JaideAiSetupCheckService aiSetupCheckService =
			new JaideAiSetupCheckService();

	public static void updateExplanation(
			Project project,
			JaideExplanation explanation
	) {
		ApplicationManager.getApplication().invokeLater(() ->
				project.getService(JaideToolWindowController.class)
						.showExplanation(explanation)
		);
	}

	public static void updateErrorExplanation(
			Project project,
			JaideErrorExplanation errorExplanation
	) {
		ApplicationManager.getApplication().invokeLater(() ->
				project.getService(JaideToolWindowController.class)
						.showErrorExplanation(errorExplanation)
		);
	}

	public static void updateImprovement(
			Project project,
			JaideImprovement improvement,
			String originalCode
	) {
		ApplicationManager.getApplication().invokeLater(() ->
				project.getService(JaideToolWindowController.class)
						.showImprovement(
								improvement,
								originalCode
						)
		);
	}

	public static void updateTestGeneration(
			Project project,
			JaideTestGenerationResult testGenerationResult
	) {
		ApplicationManager.getApplication().invokeLater(() ->
				project.getService(JaideToolWindowController.class)
						.showTestGeneration(testGenerationResult)
		);
	}

	public static void updateAiHealthLoading(Project project) {
		ApplicationManager.getApplication().invokeLater(() ->
				project.getService(JaideToolWindowController.class)
						.showAiHealthLoading()
		);
	}

	public static void updateAiHealth(
			Project project,
			JaideHealthResponse healthResponse
	) {
		ApplicationManager.getApplication().invokeLater(() ->
				project.getService(JaideToolWindowController.class)
						.showAiHealth(healthResponse)
		);
	}

	public static void updateAiHealthError(
			Project project,
			String message,
			Runnable retryAction
	) {
		ApplicationManager.getApplication().invokeLater(() ->
				project.getService(JaideToolWindowController.class)
						.showAiHealthError(
								message,
								retryAction
						)
		);
	}

	public static boolean isShowingErrorExplanation(Project project) {
		if (project == null) {
			return false;
		}

		return project.getService(JaideToolWindowController.class)
				.isShowingErrorExplanation();
	}

	@Override
	public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

		JaideToolWindowController controller =
				project.getService(JaideToolWindowController.class);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		JaideExplainModeSelectorPanel explainModeSelectorPanel =
				new JaideExplainModeSelectorPanel();
		explainModeSelectorPanel.setVisible(false);
		JPanel headerPanel = new JPanel(new BorderLayout());

		JButton checkAiSetupButton =
				new JButton(JaideUiLabels.CHECK_AI_SETUP_BUTTON);

		checkAiSetupButton.addActionListener(
				event -> aiSetupCheckService.check(project)
		);

		checkAiSetupButton.setMargin(JBUI.insets(6, 16));
		checkAiSetupButton.setPreferredSize(JBUI.size(180, 36));

		JPanel setupButtonPanel =
				new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

		setupButtonPanel.add(checkAiSetupButton);

		headerPanel.add(setupButtonPanel, BorderLayout.NORTH);
		headerPanel.add(explainModeSelectorPanel, BorderLayout.CENTER);

		panel.add(headerPanel, BorderLayout.NORTH);

		JPanel actionsPanel = new JPanel();
		actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
		actionsPanel.setVisible(false);

		JPanel primaryActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		JPanel navigationActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

		JButton showDiffButton =
				new JButton(JaideUiLabels.SHOW_DIFF_BUTTON);
		showDiffButton.addActionListener(
				event -> toolWindowActionsService.showLatestImprovementDiff(project)
		);

		JButton applyButton =
				new JButton(JaideUiLabels.APPLY_BUTTON);
		applyButton.addActionListener(
				event -> toolWindowActionsService.applyLatestImprovement(project)
		);

		JButton copyCodeButton =
				new JButton(JaideUiLabels.COPY_IMPROVED_CODE_BUTTON);
		copyCodeButton.addActionListener(event -> {
			if (controller.isShowingTestGeneration()) {
				copyGeneratedTestCodeService.copyLatestGeneratedTestCode(project);
				return;
			}

			copyImprovedCodeService.copyLatestImprovedCode(project);
		});

		JButton backToCodeButton =
				new JButton(JaideUiLabels.BACK_TO_CODE_BUTTON);
		backToCodeButton.addActionListener(
				event -> toolWindowActionsService.backToCode(project)
		);

		controller.bindActionControls(
				actionsPanel,
				explainModeSelectorPanel,
				showDiffButton,
				applyButton,
				copyCodeButton,
				backToCodeButton
		);

		primaryActionsPanel.add(showDiffButton);
		primaryActionsPanel.add(applyButton);
		primaryActionsPanel.add(copyCodeButton);

		navigationActionsPanel.add(backToCodeButton);

		actionsPanel.add(primaryActionsPanel);
		actionsPanel.add(Box.createVerticalStrut(6));
		actionsPanel.add(navigationActionsPanel);

		panel.add(actionsPanel, BorderLayout.SOUTH);

		JPanel previewContainer = new JPanel(new BorderLayout());

		JaideImprovePreviewPanel improvePreviewPanel =
				new JaideImprovePreviewPanel(project, "");
		JaideExplanationPreviewPanel explanationPreviewPanel =
				new JaideExplanationPreviewPanel("");
		JaideErrorExplanationPreviewPanel errorExplanationPreviewPanel =
				new JaideErrorExplanationPreviewPanel();
		JaideTestGenerationPreviewPanel testGenerationPreviewPanel =
				new JaideTestGenerationPreviewPanel(project);
		JaideAiHealthPreviewPanel aiHealthPreviewPanel =
				new JaideAiHealthPreviewPanel();

		previewContainer.add(improvePreviewPanel, BorderLayout.CENTER);

		controller.bindPreviewControls(
				previewContainer,
				improvePreviewPanel,
				explanationPreviewPanel,
				errorExplanationPreviewPanel,
				testGenerationPreviewPanel,
				aiHealthPreviewPanel
		);

		panel.add(previewContainer, BorderLayout.CENTER);

		Content content = ContentFactory.getInstance().createContent(panel, "", false);
		toolWindow.getContentManager().addContent(content);

		autoHideService.register(project, toolWindow);
	}
}
