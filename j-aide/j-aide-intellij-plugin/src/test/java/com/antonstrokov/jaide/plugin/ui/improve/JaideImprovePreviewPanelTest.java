package com.antonstrokov.jaide.plugin.ui.improve;

import com.antonstrokov.jaide.plugin.dto.improve.JaideImprovement;
import com.antonstrokov.jaide.plugin.service.JaideChangeVerificationResult;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JaideImprovePreviewPanelTest extends BasePlatformTestCase {

	public void testShouldShowSemanticWarningForInconsistentImprovement() {
		JaideImprovePreviewPanel panel =
				new JaideImprovePreviewPanel(
						getProject(),
						"Initial"
				);

		JaideImprovement improvement =
				new JaideImprovement(
						"Summary",
						"class Example { void main() {} }",
						List.of("Method main renamed to executeProgram"),
						null,
						"none",
						"high"
				);

		panel.updateImprovement(
				improvement,
				"class Example { void main() {} }",
				JaideChangeVerificationResult.INCONSISTENT
		);

		assertTrue(
				containsText(
						panel,
						"Structured change claims do not match the generated code."
				)
		);
	}

	private boolean containsText(
			Component component,
			String expectedText
	) {
		if (component instanceof JLabel label
				&& expectedText.equals(label.getText())) {
			return true;
		}

		if (component instanceof JTextArea textArea
				&& expectedText.equals(textArea.getText())) {
			return true;
		}

		if (component instanceof Container container) {
			for (Component child : container.getComponents()) {
				if (containsText(child, expectedText)) {
					return true;
				}
			}
		}

		return false;
	}

	public void testShouldNotShowSemanticWarningWhenImprovementIsNotVerifiable() {
		JaideImprovePreviewPanel panel =
				new JaideImprovePreviewPanel(
						getProject(),
						"Initial"
				);

		JaideImprovement improvement =
				new JaideImprovement(
						"Summary",
						"class Example { void executeProgram() {} }",
						List.of("Method main renamed to executeProgram"),
						null,
						"none",
						"high"
				);

		panel.updateImprovement(
				improvement,
				"class Example { void main() {} }",
				JaideChangeVerificationResult.NOT_VERIFIABLE
		);

		assertFalse(
				containsText(
						panel,
						"Structured change claims do not match the generated code."
				)
		);
	}
}
