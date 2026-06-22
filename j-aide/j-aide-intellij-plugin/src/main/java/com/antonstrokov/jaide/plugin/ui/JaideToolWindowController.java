package com.antonstrokov.jaide.plugin.ui;

import com.antonstrokov.jaide.plugin.config.JaideToolWindowMode;
import com.intellij.openapi.components.Service;
import com.antonstrokov.jaide.plugin.ui.settings.JaideExplainModeSelectorPanel;

import javax.swing.*;


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

	private JaideToolWindowMode currentMode;
	private JPanel actionsPanel;
	private JaideExplainModeSelectorPanel explainModeSelectorPanel;
	private JButton showDiffButton;
	private JButton applyButton;
	private JButton copyCodeButton;
	private JButton backToCodeButton;

	public void setCurrentMode(JaideToolWindowMode currentMode) {
		this.currentMode = currentMode;
	}

	public void applyExplanationViewState() {
		applyViewState(EXPLANATION_VIEW_STATE);
	}

	public void applyErrorExplanationViewState() {
		applyViewState(ERROR_EXPLANATION_VIEW_STATE);
	}

	public void applyImprovementViewState() {
		applyViewState(IMPROVEMENT_VIEW_STATE);
	}

	public void applyTestGenerationViewState() {
		applyViewState(TEST_GENERATION_VIEW_STATE);
	}

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

	private record ViewState(
			boolean explainModeSelectorVisible,
			boolean showDiffVisible,
			boolean applyVisible,
			boolean copyCodeVisible,
			boolean backToCodeVisible
	) {
	}
}
