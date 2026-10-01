package com.antonstrokov.jaide.plugin.context;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionUiKind;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class JaideEditorContextExtractorTest
		extends LightJavaCodeInsightFixtureTestCase5 {

	private static final String SOURCE = """
            class Example {
                int value = 1;
            }
            """;

	@Override
	protected String getTestDataPath() {
		return System.getProperty("java.io.tmpdir");
	}

	@Test
	void shouldExcludeNextLineWhenSelectionEndsAtItsStart() {
		EdtTestUtil.runInEdtAndWait(() -> {
			PsiFile psiFile = getFixture().configureByText(
					"Example.java",
					SOURCE
			);
			Editor editor = getFixture().getEditor();
			Document document = editor.getDocument();

			int selectionStart = document.getLineStartOffset(1);
			int selectionEnd = document.getLineStartOffset(2);

			editor.getSelectionModel().setSelection(
					selectionStart,
					selectionEnd
			);

			JaideEditorContext context = extractContext(editor, psiFile);

			assertNotNull(context);
			assertEquals("    int value = 1;\n", context.selectedCode());
			assertEquals(2, context.lineStart());
			assertEquals(2, context.lineEnd());
			assertEquals(selectionStart, context.selectionStart());
			assertEquals(selectionEnd, context.selectionEnd());
			assertSame(document, context.document());
		});
	}

	@Test
	void shouldKeepEndLineWhenSelectionEndsBeforeLineBreak() {
		EdtTestUtil.runInEdtAndWait(() -> {
			PsiFile psiFile = getFixture().configureByText(
					"Example.java",
					SOURCE
			);
			Editor editor = getFixture().getEditor();
			Document document = editor.getDocument();

			int selectionStart = document.getLineStartOffset(1);
			int selectionEnd = document.getLineEndOffset(1);

			editor.getSelectionModel().setSelection(
					selectionStart,
					selectionEnd
			);

			JaideEditorContext context = extractContext(editor, psiFile);

			assertNotNull(context);
			assertEquals("    int value = 1;", context.selectedCode());
			assertEquals(2, context.lineStart());
			assertEquals(2, context.lineEnd());
		});
	}

	@Test
	void shouldIncludeNextLineWhenItsFirstCharacterIsSelected() {
		EdtTestUtil.runInEdtAndWait(() -> {
			PsiFile psiFile = getFixture().configureByText(
					"Example.java",
					SOURCE
			);
			Editor editor = getFixture().getEditor();
			Document document = editor.getDocument();

			int selectionStart = document.getLineStartOffset(1);
			int selectionEnd = document.getLineStartOffset(2) + 1;

			editor.getSelectionModel().setSelection(
					selectionStart,
					selectionEnd
			);

			JaideEditorContext context = extractContext(editor, psiFile);

			assertNotNull(context);
			assertEquals("    int value = 1;\n}", context.selectedCode());
			assertEquals(2, context.lineStart());
			assertEquals(3, context.lineEnd());
		});
	}

	private JaideEditorContext extractContext(
			Editor editor,
			PsiFile psiFile
	) {
		DataContext dataContext = SimpleDataContext.builder()
				.add(CommonDataKeys.PROJECT, getFixture().getProject())
				.add(CommonDataKeys.EDITOR, editor)
				.add(CommonDataKeys.VIRTUAL_FILE, psiFile.getVirtualFile())
				.add(CommonDataKeys.PSI_FILE, psiFile)
				.build();

		AnActionEvent event = AnActionEvent.createEvent(
				dataContext,
				null,
				ActionPlaces.UNKNOWN,
				ActionUiKind.NONE,
				null
		);

		return new JaideEditorContextExtractor().extract(event);
	}
}
