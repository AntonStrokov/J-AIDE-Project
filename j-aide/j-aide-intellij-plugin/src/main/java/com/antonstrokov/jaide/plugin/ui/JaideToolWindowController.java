package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.config.JaideToolWindowMode;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplanation;
import com.antonstrokov.jaide.plugin.dto.explain.JaideExplanation;
import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.antonstrokov.jaide.plugin.dto.tests.JaideTestGenerationResult;
import com.antonstrokov.jaide.plugin.ui.error.JaideErrorExplanationPreviewPanel;
import com.antonstrokov.jaide.plugin.ui.explain.JaideExplanationPreviewPanel;
import com.antonstrokov.jaide.plugin.ui.improve.JaideImprovePreviewPanel;
import com.antonstrokov.jaide.plugin.ui.settings.JaideExplainModeSelectorPanel;
import com.antonstrokov.jaide.plugin.ui.tests.JaideTestGenerationPreviewPanel;
import com.antonstrokov.jaide.plugin.dto.health.JaideHealthResponse;
import com.antonstrokov.jaide.plugin.ui.health.JaideAiHealthPreviewPanel;
import com.intellij.openapi.components.Service;

import javax.swing.*;
import java.awt.*;


@Service(Service.Level.PROJECT)
public final class JaideToolWindowController {

	private static final ViewState EXPLANATION_VIEW_STATE =
			new ViewState(true, false, false, false, true);

	private static final ViewState ERROR_EXPLANATION_VIEW_STATE =
			new ViewState(false, false, false, false, true);

	private static final ViewState IMPROVEMENT_VIEW_STATE =
			new ViewState(false, true, true, true, true);

	private static final ViewState TEST_GENERATION_VIEW_STATE =
			new ViewState(false, false, false, true, true);

	private static final ViewState AI_HEALTH_VIEW_STATE =
			new ViewState(false, false, false, false, true);

	private JaideExplanation latestExplanation;
	private JaideErrorExplanation latestErrorExplanation;
	private JaideImprovement latestImprovement;
	private String latestOriginalCode;
	private JaideTestGenerationResult latestTestGenerationResult;
	private JaideHealthResponse latestHealthResponse;

	private JaideToolWindowMode currentMode;
	private JPanel actionsPanel;
	private JaideExplainModeSelectorPanel explainModeSelectorPanel;
	private JButton showDiffButton;
	private JButton applyButton;
	private JButton copyCodeButton;
	private JButton backToCodeButton;

	private JPanel previewContainer;
	private JaideImprovePreviewPanel improvePreviewPanel;
	private JaideExplanationPreviewPanel explanationPreviewPanel;
	private JaideErrorExplanationPreviewPanel errorExplanationPreviewPanel;
	private JaideTestGenerationPreviewPanel testGenerationPreviewPanel;
	private JaideAiHealthPreviewPanel aiHealthPreviewPanel;

	public boolean isShowingErrorExplanation() {
		return currentMode == JaideToolWindowMode.ERROR_EXPLANATION;
	}

	public boolean isShowingTestGeneration() {
		return currentMode == JaideToolWindowMode.TEST_GENERATION;
	}

	public void bindActionControls(
			JPanel actionsPanel,
			JaideExplainModeSelectorPanel explainModeSelectorPanel,
			JButton showDiffButton,
			JButton applyButton,
			JButton copyCodeButton,
			JButton backToCodeButton
	) {
		this.actionsPanel = actionsPanel;
		this.explainModeSelectorPanel = explainModeSelectorPanel;
		this.showDiffButton = showDiffButton;
		this.applyButton = applyButton;
		this.copyCodeButton = copyCodeButton;
		this.backToCodeButton = backToCodeButton;

		applyCurrentViewState();
	}

	private void applyViewState(ViewState viewState) {
		if (actionsPanel != null) {
			actionsPanel.setVisible(true);
		}

		if (explainModeSelectorPanel != null) {
			explainModeSelectorPanel.setVisible(viewState.explainModeSelectorVisible());
		}

		if (showDiffButton != null) {
			showDiffButton.setVisible(viewState.showDiffVisible());
		}

		if (applyButton != null) {
			applyButton.setVisible(viewState.applyVisible());
		}

		if (copyCodeButton != null) {
			copyCodeButton.setVisible(viewState.copyCodeVisible());
		}

		if (backToCodeButton != null) {
			backToCodeButton.setVisible(viewState.backToCodeVisible());
		}
	}

	public void bindPreviewControls(
			JPanel previewContainer,
			JaideImprovePreviewPanel improvePreviewPanel,
			JaideExplanationPreviewPanel explanationPreviewPanel,
			JaideErrorExplanationPreviewPanel errorExplanationPreviewPanel,
			JaideTestGenerationPreviewPanel testGenerationPreviewPanel,
			JaideAiHealthPreviewPanel aiHealthPreviewPanel
	) {
		this.previewContainer = previewContainer;
		this.improvePreviewPanel = improvePreviewPanel;
		this.explanationPreviewPanel = explanationPreviewPanel;
		this.errorExplanationPreviewPanel = errorExplanationPreviewPanel;
		this.testGenerationPreviewPanel = testGenerationPreviewPanel;
		this.aiHealthPreviewPanel = aiHealthPreviewPanel;

		if (currentMode == JaideToolWindowMode.EXPLANATION) {
			renderExplanation();
		}

		if (currentMode == JaideToolWindowMode.ERROR_EXPLANATION) {
			renderErrorExplanation();
		}

		if (currentMode == JaideToolWindowMode.IMPROVEMENT) {
			renderImprovement();
		}

		if (currentMode == JaideToolWindowMode.TEST_GENERATION) {
			renderTestGeneration();
		}

		if (currentMode == JaideToolWindowMode.AI_HEALTH) {
			renderAiHealth();
		}
	}

	public void showExplanation(JaideExplanation explanation) {
		currentMode = JaideToolWindowMode.EXPLANATION;
		latestExplanation = explanation;

		renderExplanation();
		applyCurrentViewState();
	}

	private void renderExplanation() {
		if (previewContainer == null
				|| explanationPreviewPanel == null
				|| latestExplanation == null) {
			return;
		}

		previewContainer.removeAll();
		explanationPreviewPanel.updateExplanation(latestExplanation);
		previewContainer.add(explanationPreviewPanel, BorderLayout.CENTER);
		previewContainer.revalidate();
		previewContainer.repaint();
	}

	public void showErrorExplanation(JaideErrorExplanation errorExplanation) {
		currentMode = JaideToolWindowMode.ERROR_EXPLANATION;
		latestErrorExplanation = errorExplanation;

		renderErrorExplanation();
		applyCurrentViewState();
	}

	private void renderErrorExplanation() {
		if (previewContainer == null
				|| errorExplanationPreviewPanel == null
				|| latestErrorExplanation == null) {
			return;
		}

		previewContainer.removeAll();
		errorExplanationPreviewPanel.updateErrorExplanation(latestErrorExplanation);
		previewContainer.add(errorExplanationPreviewPanel, BorderLayout.CENTER);
		previewContainer.revalidate();
		previewContainer.repaint();
	}

	public void showImprovement(
			JaideImprovement improvement,
			String originalCode
	) {
		currentMode = JaideToolWindowMode.IMPROVEMENT;
		latestImprovement = improvement;
		latestOriginalCode = originalCode;

		renderImprovement();
		applyCurrentViewState();
	}

	private void renderImprovement() {
		if (previewContainer == null
				|| improvePreviewPanel == null
				|| latestImprovement == null
				|| latestOriginalCode == null) {
			return;
		}

		previewContainer.removeAll();
		improvePreviewPanel.updateImprovement(
				latestImprovement,
				latestOriginalCode
		);
		previewContainer.add(improvePreviewPanel, BorderLayout.CENTER);
		previewContainer.revalidate();
		previewContainer.repaint();
	}

	public void showTestGeneration(
			JaideTestGenerationResult testGenerationResult
	) {
		currentMode = JaideToolWindowMode.TEST_GENERATION;
		latestTestGenerationResult = testGenerationResult;

		renderTestGeneration();
		applyCurrentViewState();
	}

	private void renderTestGeneration() {
		if (previewContainer == null
				|| testGenerationPreviewPanel == null
				|| latestTestGenerationResult == null) {
			return;
		}

		previewContainer.removeAll();
		testGenerationPreviewPanel.updateTestGeneration(
				latestTestGenerationResult
		);
		previewContainer.add(
				testGenerationPreviewPanel,
				BorderLayout.CENTER
		);
		previewContainer.revalidate();
		previewContainer.repaint();
	}

	public void showAiHealth(JaideHealthResponse healthResponse) {
		currentMode = JaideToolWindowMode.AI_HEALTH;
		latestHealthResponse = healthResponse;

		renderAiHealth();
		applyCurrentViewState();
	}

	private void renderAiHealth() {
		if (previewContainer == null
				|| aiHealthPreviewPanel == null
				|| latestHealthResponse == null) {
			return;
		}

		previewContainer.removeAll();
		aiHealthPreviewPanel.updateHealth(latestHealthResponse);
		previewContainer.add(aiHealthPreviewPanel, BorderLayout.CENTER);
		previewContainer.revalidate();
		previewContainer.repaint();
	}

	private void applyCurrentViewState() {
		if (currentMode == null) {
			return;
		}

		switch (currentMode) {
			case EXPLANATION -> applyViewState(EXPLANATION_VIEW_STATE);
			case ERROR_EXPLANATION -> applyViewState(ERROR_EXPLANATION_VIEW_STATE);
			case IMPROVEMENT -> applyViewState(IMPROVEMENT_VIEW_STATE);
			case TEST_GENERATION -> applyViewState(TEST_GENERATION_VIEW_STATE);
			case AI_HEALTH -> applyViewState(AI_HEALTH_VIEW_STATE);
		}
	}

	private record ViewState(
			boolean explainModeSelectorVisible,
			boolean showDiffVisible,
			boolean applyVisible,
			boolean copyCodeVisible,
			boolean backToCodeVisible
	) {
	}
}
