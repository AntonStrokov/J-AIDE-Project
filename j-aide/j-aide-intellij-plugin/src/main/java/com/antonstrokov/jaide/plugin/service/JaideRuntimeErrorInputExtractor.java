package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.context.JaideEditorContextExtractor;
import com.antonstrokov.jaide.plugin.model.JaideRuntimeErrorInput;
import com.antonstrokov.jaide.plugin.model.JaideRuntimeErrorInputSource;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;

import java.awt.datatransfer.DataFlavor;

public class JaideRuntimeErrorInputExtractor {
	private static final Logger log = Logger.getInstance(JaideRuntimeErrorInputExtractor.class);

	private final JaideEditorContextExtractor contextExtractor = new JaideEditorContextExtractor();

	public JaideRuntimeErrorInput extract(AnActionEvent event) {
		JaideEditorContext context = contextExtractor.extract(event);

		if (context != null) {
			log.info("Runtime error input extracted from editor selection, fileName=" + context.fileName()
					+ ", textLength=" + context.selectedCode().length()
					+ ", lineStart=" + context.lineStart()
					+ ", lineEnd=" + context.lineEnd());
			JaideRuntimeErrorInputSource source =
					event.getData(CommonDataKeys.PSI_FILE) == null
							? JaideRuntimeErrorInputSource.CONSOLE_SELECTION
							: JaideRuntimeErrorInputSource.EDITOR_SELECTION;

			return new JaideRuntimeErrorInput(
					context.selectedCode(),
					context.fileName(),
					context.lineStart(),
					context.lineEnd(),
					context.projectName(),
					context.ideVersion(),
					context.moduleName(),
					source
			);
		}

		String clipboardText = getClipboardText();

		if (clipboardText == null || clipboardText.isBlank()) {
			log.warn("Runtime error input was not found: no editor selection and clipboard is empty");
			return null;
		}

		Project project = event.getProject();

		log.info("Runtime error input extracted from clipboard, textLength=" + clipboardText.length());

		return new JaideRuntimeErrorInput(
				clipboardText,
				null,
				null,
				null,
				project == null ? null : project.getName(),
				ApplicationInfo.getInstance().getFullVersion(),
				null,
				JaideRuntimeErrorInputSource.CLIPBOARD
		);
	}

	private String getClipboardText() {
		try {
			Object clipboardContent = CopyPasteManager.getInstance().getContents(DataFlavor.stringFlavor);

			if (clipboardContent instanceof String text) {
				return text;
			}

			return null;
		} catch (Exception ex) {
			log.warn("Cannot read clipboard text: " + ex.getMessage(), ex);
			return null;
		}
	}
}