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
import com.antonstrokov.jaide.plugin.ui.layout.JaideWrapLayout;
import com.antonstrokov.jaide.plugin.ui.settings.JaideExplainModeSelectorPanel;
import com.antonstrokov.jaide.plugin.ui.tests.JaideTestGenerationPreviewPanel;
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
			JaideHealthResponse healthResponse,
			Runnable retryAction
	) {
		ApplicationManager.getApplication().invokeLater(() ->
				project.getService(JaideToolWindowController.class)
						.showAiHealth(
								healthResponse,
								retryAction
						)
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
				JaideActionButtonFactory.create(
						JaideUiLabels.CHECK_AI_SETUP_BUTTON
				);

		checkAiSetupButton.addActionListener(
				event -> aiSetupCheckService.check(project)
		);

		JPanel setupButtonPanel =
				new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

		setupButtonPanel.add(checkAiSetupButton);

		headerPanel.add(setupButtonPanel, BorderLayout.NORTH);
		headerPanel.add(explainModeSelectorPanel, BorderLayout.CENTER);

		panel.add(headerPanel, BorderLayout.NORTH);

		JPanel actionsPanel = new JPanel(
				new JaideWrapLayout(
						FlowLayout.LEFT,
						8,
						6
				)
		);
		actionsPanel.setVisible(false);


		JButton showDiffButton =
				JaideActionButtonFactory.create(
						JaideUiLabels.SHOW_DIFF_BUTTON
				);
		showDiffButton.addActionListener(
				event -> toolWindowActionsService.showLatestImprovementDiff(project)
		);

		JButton applyButton =
				JaideActionButtonFactory.create(
						JaideUiLabels.APPLY_BUTTON
				);
		applyButton.addActionListener(
				event -> toolWindowActionsService.applyLatestImprovement(project)
		);

		JButton copyCodeButton =
				JaideActionButtonFactory.create(
						JaideUiLabels.COPY_IMPROVED_CODE_BUTTON
				);
		copyCodeButton.addActionListener(event -> {
			if (controller.isShowingTestGeneration()) {
				copyGeneratedTestCodeService.copyLatestGeneratedTestCode(project);
				return;
			}

			copyImprovedCodeService.copyLatestImprovedCode(project);
		});

		JButton backToCodeButton =
				JaideActionButtonFactory.create(
						JaideUiLabels.BACK_TO_CODE_BUTTON
				);
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

		actionsPanel.add(showDiffButton);
		actionsPanel.add(applyButton);
		actionsPanel.add(copyCodeButton);
		actionsPanel.add(backToCodeButton);

		panel.add(actionsPanel, BorderLayout.SOUTH);

		JPanel previewContainer = new JPanel(new BorderLayout());

		JaideEmptyStatePanel emptyStatePanel =
				new JaideEmptyStatePanel(
						() -> toolWindowActionsService.backToCode(project)
				);

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

		previewContainer.add(emptyStatePanel, BorderLayout.CENTER);

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
