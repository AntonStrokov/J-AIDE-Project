package com.antonstrokov.jaide.plugin.ui.diff;

import com.antonstrokov.jaide.plugin.service.JaideApplyImprovementService;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestPanel;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class JaideDiffDialog extends DialogWrapper {

	private final Project project;
	private final DiffRequest diffRequest;
	private final JaideApplyImprovementService applyImprovementService;

	public JaideDiffDialog(
			@NotNull Project project,
			@NotNull DiffRequest diffRequest,
			@NotNull JaideApplyImprovementService applyImprovementService
	) {
		super(project, true);
		this.project = project;
		this.diffRequest = diffRequest;
		this.applyImprovementService = applyImprovementService;

		setTitle("J-Aide Improve Diff");
		init();
	}

	@Override
	protected @Nullable JComponent createCenterPanel() {
		DiffRequestPanel diffRequestPanel = DiffManager.getInstance().createRequestPanel(
				project,
				getDisposable(),
				getWindow()
		);

		diffRequestPanel.setRequest(diffRequest);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(diffRequestPanel.getComponent(), BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(1100, 700));

		return panel;
	}

	@Override
	protected @Nullable JComponent createSouthPanel() {
		JButton applyButton = new JButton("Apply Improvement");
		applyButton.addActionListener(event -> {
			applyImprovementService.applyLatestImprovement(project);
			close(OK_EXIT_CODE);
		});

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(event -> close(CANCEL_EXIT_CODE));

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(applyButton);
		panel.add(closeButton);

		return panel;
	}
}