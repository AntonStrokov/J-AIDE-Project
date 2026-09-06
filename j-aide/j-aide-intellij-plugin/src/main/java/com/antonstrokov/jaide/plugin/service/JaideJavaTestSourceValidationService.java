package com.antonstrokov.jaide.plugin.service;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.openapi.application.ReadAction;

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

	public boolean hasSelectedClassReferenceErrors(
			Project project,
			Document sourceDocument,
			int selectionStart,
			String testCode
	) {
		if (testCode == null || testCode.isBlank()) {
			return false;
		}

		PsiDocumentManager psiDocumentManager =
				PsiDocumentManager.getInstance(project);

		return psiDocumentManager.commitAndRunReadAction(() -> {
			PsiFile sourceFile =
					psiDocumentManager.getPsiFile(sourceDocument);

			if (sourceFile == null) {
				return false;
			}

			PsiElement elementAtSelection =
					sourceFile.findElementAt(selectionStart);

			PsiClass sourceClass =
					PsiTreeUtil.getParentOfType(
							elementAtSelection,
							PsiClass.class
					);

			if (sourceClass == null || sourceClass.getName() == null) {
				return false;
			}

			PsiFile generatedFile =
					PsiFileFactory.getInstance(project)
							.createFileFromText(
									"GeneratedTest.java",
									JavaFileType.INSTANCE,
									testCode
							);

			return PsiTreeUtil.findChildrenOfType(
							generatedFile,
							PsiJavaCodeReferenceElement.class
					).stream()
					.filter(reference ->
							sourceClass.getName().equals(
									reference.getReferenceName()
							)
					)
					.anyMatch(reference -> {
						PsiElement resolved =
								reference.resolve();

						return resolved == null
								|| !PsiManager.getInstance(project)
								.areElementsEquivalent(
										sourceClass,
										resolved
								);
					});
		});
	}
}
