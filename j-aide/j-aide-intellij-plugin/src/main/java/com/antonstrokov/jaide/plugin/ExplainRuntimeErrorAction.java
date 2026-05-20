package com.antonstrokov.jaide.plugin;

import com.antonstrokov.jaide.plugin.client.JaideBackendClient;
import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.antonstrokov.jaide.plugin.context.JaideEditorContext;
import com.antonstrokov.jaide.plugin.context.JaideEditorContextExtractor;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplainRequest;
import com.antonstrokov.jaide.plugin.dto.error.JaideErrorExplanation;
import com.antonstrokov.jaide.plugin.error.JaideErrorMessageBuilder;
import com.antonstrokov.jaide.plugin.notification.JaideNotificationService;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowFactory;
import com.antonstrokov.jaide.plugin.ui.JaideToolWindowService;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;

public class ExplainRuntimeErrorAction extends AnAction {
	private static final Logger log = Logger.getInstance(ExplainRuntimeErrorAction.class);

	private final JaideBackendClient backendClient = new JaideBackendClient();
	private final JaideEditorContextExtractor contextExtractor = new JaideEditorContextExtractor();
	private final JaideErrorMessageBuilder errorMessageBuilder = new JaideErrorMessageBuilder();
	private final JaideNotificationService notificationService = new JaideNotificationService();
	private final JaideToolWindowService toolWindowService = new JaideToolWindowService();

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		log.info("Explain runtime error action started");

		ErrorInput errorInput = extractErrorInput(e);

		if (errorInput == null) {
			log.warn("Explain runtime error action stopped: no selected error text and clipboard is empty");
			notificationService.showWarning(
					e.getProject(),
					"Please select error text or copy stack trace to clipboard first"
			);
			return;
		}

		if (!looksLikeErrorText(errorInput.errorText())) {
			log.warn("Explain runtime error action stopped: input does not look like error text, source="
					+ errorInput.source()
					+ ", textLength=" + errorInput.errorText().length());

			notificationService.showWarning(
					e.getProject(),
					"Selected text does not look like an error, stack trace, or log. Use J-Aide: Explain Selected Code for source code."
			);
			return;
		}

		log.info("Runtime error input extracted, source=" + errorInput.source()
				+ ", fileName=" + errorInput.fileName()
				+ ", errorTextLength=" + errorInput.errorText().length()
				+ ", lineStart=" + errorInput.lineStart()
				+ ", lineEnd=" + errorInput.lineEnd());

		new Task.Backgroundable(e.getProject(), JaideConstants.EXPLAIN_ERROR_TASK_TITLE, false) {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					log.info("Creating explain runtime error request");

					JaideErrorExplainRequest request = createRequest(errorInput);

					log.info("Sending explain runtime error request");

					JaideErrorExplanation errorExplanation = backendClient.explainError(request);

					log.info("Explain runtime error response processed, updating tool window");

					toolWindowService.open(e.getProject());
					JaideToolWindowFactory.updateErrorExplanation(errorExplanation);

					log.info("Explain runtime error tool window updated");

				} catch (Exception ex) {
					log.warn("Explain runtime error action failed: " + ex.getMessage(), ex);

					notificationService.showError(
							e.getProject(),
							errorMessageBuilder.build(ex)
					);
				}
			}
		}.queue();
	}

	private ErrorInput extractErrorInput(AnActionEvent e) {
		JaideEditorContext context = contextExtractor.extract(e);

		if (context != null) {
			return new ErrorInput(
					context.selectedCode(),
					context.fileName(),
					context.lineStart(),
					context.lineEnd(),
					context.projectName(),
					context.ideVersion(),
					context.moduleName(),
					"editor-selection"
			);
		}

		String clipboardText = getClipboardText();

		if (clipboardText == null || clipboardText.isBlank()) {
			return null;
		}

		Project project = e.getProject();

		return new ErrorInput(
				clipboardText,
				null,
				null,
				null,
				project == null ? null : project.getName(),
				ApplicationInfo.getInstance().getFullVersion(),
				null,
				"clipboard"
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

	private boolean looksLikeErrorText(String text) {
		if (text == null || text.isBlank()) {
			return false;
		}

		String normalizedText = text.toLowerCase();

		return normalizedText.contains("exception")
				|| normalizedText.contains("error")
				|| normalizedText.contains("caused by:")
				|| normalizedText.contains("at ")
				|| normalizedText.contains("build failed")
				|| normalizedText.contains("compilation error")
				|| normalizedText.contains("failed to start")
				|| normalizedText.contains("port already in use")
				|| normalizedText.contains("connection refused")
				|| normalizedText.contains("beancreationexception")
				|| normalizedText.contains("nullpointerexception")
				|| normalizedText.contains("illegalargumentexception")
				|| normalizedText.contains("sqlexception")
				|| normalizedText.contains("unsatisfieddependencyexception")
				|| normalizedText.contains("application run failed")
				|| normalizedText.contains("cannot find symbol")
				|| normalizedText.contains("cannot resolve symbol")
				|| normalizedText.contains("class, interface, enum, or record expected")
				|| normalizedText.contains("illegal character")
				|| normalizedText.contains("illegal unicode escape")
				|| normalizedText.contains("preview feature")
				|| normalizedText.contains("disabled by default")
				|| normalizedText.contains("enable-preview")
				|| normalizedText.contains("source option")
				|| normalizedText.contains("target option")
				|| normalizedText.contains("release version")
				|| normalizedText.contains("package does not exist")
				|| normalizedText.contains("method does not override")
				|| normalizedText.contains("incompatible types")
				|| normalizedText.contains("symbol:")
				|| normalizedText.contains("location:");
	}

	private JaideErrorExplainRequest createRequest(ErrorInput input) {
		return new JaideErrorExplainRequest(
				input.errorText(),
				"runtime_error",
				null,
				input.fileName(),
				input.lineStart(),
				input.lineEnd(),
				input.projectName(),
				null,
				input.ideVersion(),
				JaideConstants.PLUGIN_VERSION,
				input.moduleName()
		);
	}

	private record ErrorInput(
			String errorText,
			String fileName,
			Integer lineStart,
			Integer lineEnd,
			String projectName,
			String ideVersion,
			String moduleName,
			String source
	) {
	}
}