package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.dto.improve.JaideImproveChangeFact;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;
import java.util.Objects;

public class JaideImprovementSemanticValidationService {

	public JaideChangeVerificationResult verifyJavaChangeFacts(
			Project project,
			String originalCode,
			String improvedCode,
			List<JaideImproveChangeFact> changeFacts
	) {
		if (changeFacts == null || changeFacts.isEmpty()) {
			return JaideChangeVerificationResult.NOT_VERIFIABLE;
		}

		boolean hasNotVerifiableFact = false;

		for (JaideImproveChangeFact changeFact : changeFacts) {
			if (changeFact == null
					|| !"rename".equals(changeFact.operation())
					|| !"method".equals(changeFact.symbolKind())) {
				hasNotVerifiableFact = true;
				continue;
			}

			JaideChangeVerificationResult result =
					verifyJavaMethodRename(
							project,
							originalCode,
							improvedCode,
							changeFact.before(),
							changeFact.after()
					);

			if (result == JaideChangeVerificationResult.INCONSISTENT) {
				return JaideChangeVerificationResult.INCONSISTENT;
			}

			if (result == JaideChangeVerificationResult.NOT_VERIFIABLE) {
				hasNotVerifiableFact = true;
			}
		}

		return hasNotVerifiableFact
				? JaideChangeVerificationResult.NOT_VERIFIABLE
				: JaideChangeVerificationResult.CONSISTENT;
	}

	public JaideChangeVerificationResult verifyJavaMethodRename(
			Project project,
			String originalCode,
			String improvedCode,
			String before,
			String after
	) {
		if (project == null
				|| originalCode == null
				|| originalCode.isBlank()
				|| improvedCode == null
				|| improvedCode.isBlank()
				|| before == null
				|| before.isBlank()
				|| after == null
				|| after.isBlank()
				|| before.equals(after)) {
			return JaideChangeVerificationResult.NOT_VERIFIABLE;
		}

		return ReadAction.compute(() -> {
			PsiJavaFile originalFile = createJavaFile(
					project,
					"Original.java",
					originalCode
			);

			PsiJavaFile improvedFile = createJavaFile(
					project,
					"Improved.java",
					improvedCode
			);

			if (originalFile == null
					|| improvedFile == null
					|| hasSyntaxErrors(originalFile)
					|| hasSyntaxErrors(improvedFile)) {
				return JaideChangeVerificationResult.NOT_VERIFIABLE;
			}

			PsiClass[] originalClasses = originalFile.getClasses();
			PsiClass[] improvedClasses = improvedFile.getClasses();

			if (originalClasses.length != 1 || improvedClasses.length != 1) {
				return JaideChangeVerificationResult.NOT_VERIFIABLE;
			}

			PsiClass originalClass = originalClasses[0];
			PsiClass improvedClass = improvedClasses[0];

			if (!Objects.equals(
					originalClass.getName(),
					improvedClass.getName()
			)) {
				return JaideChangeVerificationResult.NOT_VERIFIABLE;
			}

			PsiMethod[] originalBefore =
					originalClass.findMethodsByName(before, false);
			PsiMethod[] originalAfter =
					originalClass.findMethodsByName(after, false);
			PsiMethod[] improvedBefore =
					improvedClass.findMethodsByName(before, false);
			PsiMethod[] improvedAfter =
					improvedClass.findMethodsByName(after, false);

			if (originalBefore.length == 1
					&& originalAfter.length == 0
					&& improvedBefore.length == 1
					&& improvedAfter.length == 0) {
				return JaideChangeVerificationResult.INCONSISTENT;
			}

			if (originalBefore.length != 1
					|| originalAfter.length != 0
					|| improvedBefore.length != 0
					|| improvedAfter.length != 1) {
				return JaideChangeVerificationResult.NOT_VERIFIABLE;
			}

			if (!hasSameMethodShape(
					originalBefore[0],
					improvedAfter[0]
			)) {
				return JaideChangeVerificationResult.NOT_VERIFIABLE;
			}

			return JaideChangeVerificationResult.CONSISTENT;
		});
	}

	private PsiJavaFile createJavaFile(
			Project project,
			String fileName,
			String code
	) {
		PsiFile psiFile = PsiFileFactory.getInstance(project)
				.createFileFromText(
						fileName,
						JavaFileType.INSTANCE,
						code
				);

		if (psiFile instanceof PsiJavaFile psiJavaFile) {
			return psiJavaFile;
		}

		return null;
	}

	private boolean hasSyntaxErrors(PsiJavaFile psiJavaFile) {
		return PsiTreeUtil.findChildOfType(
				psiJavaFile,
				PsiErrorElement.class
		) != null;
	}

	private boolean hasSameMethodShape(
			PsiMethod originalMethod,
			PsiMethod improvedMethod
	) {
		if (originalMethod.isConstructor()
				|| improvedMethod.isConstructor()) {
			return false;
		}

		if (!Objects.equals(
				originalMethod.getReturnType(),
				improvedMethod.getReturnType()
		)) {
			return false;
		}

		PsiParameter[] originalParameters =
				originalMethod.getParameterList().getParameters();
		PsiParameter[] improvedParameters =
				improvedMethod.getParameterList().getParameters();

		if (originalParameters.length != improvedParameters.length) {
			return false;
		}

		for (int i = 0; i < originalParameters.length; i++) {
			if (!Objects.equals(
					originalParameters[i].getType(),
					improvedParameters[i].getType()
			)) {
				return false;
			}
		}

		return true;
	}
}
