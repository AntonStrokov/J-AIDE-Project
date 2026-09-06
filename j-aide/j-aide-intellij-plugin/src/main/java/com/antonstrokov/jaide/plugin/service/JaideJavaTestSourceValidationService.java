package com.antonstrokov.jaide.plugin.service;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiTreeUtil;

public class JaideJavaTestSourceValidationService {

	public boolean hasSyntaxErrors(Project project, String testCode) {
		if (testCode == null || testCode.isBlank()) {
			return false;
		}

		return ReadAction.compute(() -> {
			PsiFile psiFile = PsiFileFactory.getInstance(project)
					.createFileFromText(
							"GeneratedTest.java",
							JavaFileType.INSTANCE,
							testCode
					);

			return PsiTreeUtil.findChildOfType(
					psiFile,
					PsiErrorElement.class
			) != null;
		});
	}

	public boolean hasTopLevelTypeDeclaration(
			Project project,
			String testCode
	) {
		if (testCode == null || testCode.isBlank()) {
			return false;
		}

		return ReadAction.compute(() -> {
			PsiFile psiFile = PsiFileFactory.getInstance(project)
					.createFileFromText(
							"GeneratedTest.java",
							JavaFileType.INSTANCE,
							testCode
					);

			if (!(psiFile instanceof PsiJavaFile psiJavaFile)) {
				return false;
			}

			return psiJavaFile.getClasses().length > 0;
		});
	}
}
