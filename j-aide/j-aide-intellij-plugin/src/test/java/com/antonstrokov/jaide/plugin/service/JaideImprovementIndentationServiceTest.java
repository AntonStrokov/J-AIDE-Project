package com.antonstrokov.jaide.plugin.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaideImprovementIndentationServiceTest {

	private final JaideImprovementIndentationService service =
			new JaideImprovementIndentationService();

	@Test
	void restoresIndentationOfSelectedMethodAndPreservesLineEndings() {
		String original = "    void x() {\r\n"
				+ "        System.out.println(\"Hello\");\r\n"
				+ "    }";
		String proposed = "void printGreeting() {\r\n"
				+ "    System.out.println(\"Hello\");\r\n"
				+ "}";
		String expected = "    void printGreeting() {\r\n"
				+ "        System.out.println(\"Hello\");\r\n"
				+ "    }";

		assertEquals(expected, service.alignWithSelection(original, proposed));
	}

	@Test
	void leavesAlreadyAlignedCodeUnchanged() {
		String original = "    void x() {\n"
				+ "        System.out.println(\"Hello\");\n"
				+ "    }";
		String proposed = "    void printGreeting() {\n"
				+ "        System.out.println(\"Hello\");\n"
				+ "    }";

		assertEquals(proposed, service.alignWithSelection(original, proposed));
	}

	@Test
	void treatsOnlyRemovedCommonIndentationAsNoOp() {
		String original = "    void x() {\n"
				+ "        System.out.println(\"Hello\");\n"
				+ "    }";
		String proposed = "void x() {\n"
				+ "    System.out.println(\"Hello\");\n"
				+ "}";

		String aligned = service.alignWithSelection(original, proposed);

		assertTrue(new JaideImprovementValidationService()
				.isNoOpImprovement(original, aligned));
	}
}
