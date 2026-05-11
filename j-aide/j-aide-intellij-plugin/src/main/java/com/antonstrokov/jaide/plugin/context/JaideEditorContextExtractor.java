package com.antonstrokov.jaide.plugin.context;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class JaideEditorContextExtractor {

	public JaideEditorContext extract(AnActionEvent e) {
		Editor editor = e.getData(CommonDataKeys.EDITOR);

		if (editor == null) {
			return null;
		}

		VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
		Project project = e.getProject();

		String fileName = virtualFile == null ? null : virtualFile.getName();
		String projectName = project == null ? null : project.getName();

		Module module = virtualFile == null || project == null
				? null
				: ModuleUtilCore.findModuleForFile(virtualFile, project);

		String moduleName = module == null ? null : module.getName();

		SelectionModel selectionModel = editor.getSelectionModel();
		String selectedText = selectionModel.getSelectedText();

		if (selectedText == null || selectedText.isBlank()) {
			return null;
		}

		int selectionStart = selectionModel.getSelectionStart();
		int selectionEnd = selectionModel.getSelectionEnd();

		int lineStart = editor.getDocument().getLineNumber(selectionStart) + 1;
		int lineEnd = editor.getDocument().getLineNumber(selectionEnd) + 1;

		String ideVersion = ApplicationInfo.getInstance().getFullVersion();

		return new JaideEditorContext(
				selectedText,
				fileName,
				lineStart,
				lineEnd,
				selectionStart,
				selectionEnd,
				projectName,
				moduleName,
				ideVersion,
				editor.getDocument()
		);
	}
}