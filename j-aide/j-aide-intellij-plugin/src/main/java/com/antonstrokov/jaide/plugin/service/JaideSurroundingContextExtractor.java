package com.antonstrokov.jaide.plugin.service;

public class JaideSurroundingContextExtractor {

	private static final String SELECTED_CODE_MARKER = "<SELECTED_CODE>";

	public String extract(
			String documentText,
			int selectionStart,
			int selectionEnd,
			int maxLength
	) {
		if (documentText == null || documentText.isEmpty() || maxLength <= 0) {
			return "";
		}

		if (selectionStart < 0
				|| selectionEnd < selectionStart
				|| selectionEnd > documentText.length()) {
			throw new IllegalArgumentException("Invalid selection range");
		}

		int availableBefore = selectionStart;
		int availableAfter = documentText.length() - selectionEnd;
		int targetLength = Math.min(
				maxLength,
				availableBefore + availableAfter
		);

		if (targetLength == 0) {
			return "";
		}

		int beforeLength = Math.min(availableBefore, targetLength / 2);
		int afterLength = Math.min(
				availableAfter,
				targetLength - beforeLength
		);

		int remaining = targetLength - beforeLength - afterLength;

		if (remaining > 0) {
			int extraBefore = Math.min(
					availableBefore - beforeLength,
					remaining
			);
			beforeLength += extraBefore;
			remaining -= extraBefore;
		}

		if (remaining > 0) {
			afterLength += Math.min(
					availableAfter - afterLength,
					remaining
			);
		}

		String before = documentText.substring(
				selectionStart - beforeLength,
				selectionStart
		);

		String after = documentText.substring(
				selectionEnd,
				selectionEnd + afterLength
		);

		return before + SELECTED_CODE_MARKER + after;
	}
}
