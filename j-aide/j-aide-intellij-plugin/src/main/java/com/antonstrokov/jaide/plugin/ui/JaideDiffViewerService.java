package com.antonstrokov.jaide.plugin.ui;

import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.project.Project;

public class JaideDiffViewerService {

	public void showImproveDiff(
			Project project,
			String originalCode,
			String improvedCode,
			String fileName
	) {
		if (project == null || originalCode == null || improvedCode == null) {
			return;
		}

		DiffContentFactory contentFactory = DiffContentFactory.getInstance();

		DiffContent originalContent = contentFactory.create(project, originalCode);
		DiffContent improvedContent = contentFactory.create(project, improvedCode);

		SimpleDiffRequest request = new SimpleDiffRequest(
				buildTitle(fileName),
				originalContent,
				improvedContent,
				"Original",
				"Improved"
		);

		DiffManager.getInstance().showDiff(project, request);
	}

	private String buildTitle(String fileName) {
		if (fileName == null || fileName.isBlank()) {
			return "J-Aide Improve Diff";
		}

		return "J-Aide Improve Diff: " + fileName;
	}
}