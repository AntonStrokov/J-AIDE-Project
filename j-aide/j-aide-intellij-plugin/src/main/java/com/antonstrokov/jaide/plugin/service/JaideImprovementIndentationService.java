package com.antonstrokov.jaide.plugin.service;

public class JaideImprovementIndentationService {

	public String alignWithSelection(String originalCode, String improvedCode) {
		if (originalCode == null || improvedCode == null) {
			return improvedCode;
		}

		String originalIndent = commonIndent(originalCode);
		String improvedIndent = commonIndent(improvedCode);

		if (originalIndent.isEmpty()
				|| !originalIndent.startsWith(improvedIndent)
				|| originalIndent.equals(improvedIndent)) {
			return improvedCode;
		}

		String missingIndent = originalIndent.substring(improvedIndent.length());

		return improvedCode.replaceAll(
				"(?m)^(?=[ \\t]*\\S)",
				missingIndent
		);
	}

	private String commonIndent(String code) {
		String common = null;

		for (String line : code.split("\\R", -1)) {
			if (line.isBlank()) {
				continue;
			}

			int indentEnd = 0;

			while (indentEnd < line.length()
					&& (line.charAt(indentEnd) == ' '
					|| line.charAt(indentEnd) == '\t')) {
				indentEnd++;
			}

			String indent = line.substring(0, indentEnd);

			if (common == null) {
				common = indent;
			} else {
				while (!indent.startsWith(common)) {
					common = common.substring(0, common.length() - 1);
				}
			}
		}

		return common == null ? "" : common;
	}
}
