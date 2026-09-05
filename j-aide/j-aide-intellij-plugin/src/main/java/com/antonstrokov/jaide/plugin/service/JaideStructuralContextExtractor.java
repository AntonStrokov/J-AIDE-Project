package com.antonstrokov.jaide.plugin.service;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.uast.*;

import java.util.stream.Collectors;

public class JaideStructuralContextExtractor {

	public String extract(
			Project project,
			Document document,
			int selectionStart,
			int selectionEnd
	) {
		PsiDocumentManager psiDocumentManager =
				PsiDocumentManager.getInstance(project);

		return psiDocumentManager.commitAndRunReadAction(() ->
				extractInsideReadAction(
						project,
						document,
						selectionStart,
						selectionEnd
				)
		);
	}

	private String extractInsideReadAction(
			Project project,
			Document document,
			int selectionStart,
			int selectionEnd
	) {
		if (selectionStart < 0
				|| selectionEnd <= selectionStart
				|| selectionEnd > document.getTextLength()) {
			return "";
		}

		int lookupOffset = findFirstNonWhitespaceOffset(
				document,
				selectionStart,
				selectionEnd
		);

		if (lookupOffset < 0) {
			return "";
		}

		PsiDocumentManager psiDocumentManager =
				PsiDocumentManager.getInstance(project);

		PsiFile psiFile = psiDocumentManager.getPsiFile(document);

		if (psiFile == null) {
			return "";
		}

		UFile uFile = UastContextKt.toUElement(
				psiFile,
				UFile.class
		);

		PsiElement elementAtSelectionStart =
				psiFile.findElementAt(lookupOffset);

		if (uFile == null || elementAtSelectionStart == null) {
			return "";
		}

		UMethod uMethod = UastContextKt.getUastParentOfType(
				elementAtSelectionStart,
				UMethod.class
		);

		if (uMethod == null) {
			return "";
		}

		UClass uClass = findEnclosingClass(uMethod);

		if (uClass == null) {
			return "";
		}

		return formatStructuralContext(
				uFile,
				uClass,
				uMethod
		);
	}

	private int findFirstNonWhitespaceOffset(
			Document document,
			int selectionStart,
			int selectionEnd
	) {
		CharSequence chars = document.getCharsSequence();

		for (int offset = selectionStart; offset < selectionEnd; offset++) {
			if (!Character.isWhitespace(chars.charAt(offset))) {
				return offset;
			}
		}

		return -1;
	}

	private String formatStructuralContext(
			UFile uFile,
			UClass uClass,
			UMethod uMethod
	) {
		String parameters = uMethod.getUastParameters()
				.stream()
				.map(this::formatParameter)
				.collect(Collectors.joining(", "));

		String returnType = uMethod.getReturnType() == null
				? ""
				: uMethod.getReturnType().getPresentableText() + " ";

		String classKind = resolveClassKind(uClass);

		String packageName = uFile.getPackageName();

		String packageLine = packageName.isBlank()
				? ""
				: "package " + packageName + "\n";

		return packageLine
				+ "%s %s\n%s%s(%s)".formatted(
				classKind,
				uClass.getName(),
				returnType,
				uMethod.getName(),
				parameters
		);
	}

	private String formatParameter(UParameter parameter) {
		return parameter.getType().getPresentableText()
				+ " "
				+ parameter.getName();
	}

	private String resolveClassKind(UClass uClass) {
		PsiClass javaPsi = uClass.getJavaPsi();

		if (javaPsi.isAnnotationType()) {
			return "@interface";
		}

		if (javaPsi.isInterface()) {
			return "interface";
		}

		if (javaPsi.isEnum()) {
			return "enum";
		}

		if (javaPsi.isRecord()) {
			return "record";
		}

		return "class";
	}

	private UClass findEnclosingClass(UMethod uMethod) {
		UElement parent = uMethod.getUastParent();

		while (parent != null) {
			if (parent instanceof UClass uClass) {
				return uClass;
			}

			parent = parent.getUastParent();
		}

		return null;
	}
}
