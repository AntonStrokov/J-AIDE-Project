package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.config.JaideConstants;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageDialogBuilder;

import java.util.function.Predicate;

public class JaideTestGenerationConsentService {

	static final String CONSENT_KEY =
			"com.antonstrokov.jaide.generateTests.dataDisclosure.v1";

	private final Predicate<Project> approvalPrompt;

	public JaideTestGenerationConsentService() {
		this(JaideTestGenerationConsentService::askUser);
	}

	JaideTestGenerationConsentService(Predicate<Project> approvalPrompt) {
		this.approvalPrompt = approvalPrompt;
	}

	public boolean confirm(Project project) {
		PropertiesComponent properties =
				PropertiesComponent.getInstance(project);

		if ("true".equals(properties.getValue(CONSENT_KEY))) {
			return true;
		}

		if (!approvalPrompt.test(project)) {
			return false;
		}

		properties.setValue(CONSENT_KEY, "true");
		return true;
	}

	private static boolean askUser(Project project) {
		String message =
				"Selected code, package/class names and method signature "
						+ "(possibly outside selection), project/module/file names, "
						+ "and line numbers go to "
						+ JaideConstants.BACKEND_BASE_URL
						+ " and its configured AI provider. Plugin/IDE versions go to "
						+ "the backend. Approval is saved for this project.";

		return MessageDialogBuilder
				.yesNo("Generate Tests: Data Sharing", message)
				.yesText("Allow and Generate Tests")
				.noText("Cancel")
				.ask(project);
	}
}
