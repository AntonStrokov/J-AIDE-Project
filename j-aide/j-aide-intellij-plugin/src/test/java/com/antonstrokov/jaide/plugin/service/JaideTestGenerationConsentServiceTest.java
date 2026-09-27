package com.antonstrokov.jaide.plugin.service;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaideTestGenerationConsentServiceTest
		extends LightJavaCodeInsightFixtureTestCase5 {

	@Override
	protected String getTestDataPath() {
		return System.getProperty("java.io.tmpdir");
	}

	@Test
	void shouldAskAgainAfterRefusalAndRememberApproval() {
		PropertiesComponent properties =
				PropertiesComponent.getInstance(getFixture().getProject());

		properties.unsetValue(JaideTestGenerationConsentService.CONSENT_KEY);

		try {
			AtomicInteger refusalPrompts = new AtomicInteger();
			JaideTestGenerationConsentService refusingService =
					new JaideTestGenerationConsentService(project -> {
						refusalPrompts.incrementAndGet();
						return false;
					});

			assertFalse(refusingService.confirm(getFixture().getProject()));
			assertFalse(refusingService.confirm(getFixture().getProject()));
			assertEquals(2, refusalPrompts.get());

			AtomicInteger approvalPrompts = new AtomicInteger();
			JaideTestGenerationConsentService approvingService =
					new JaideTestGenerationConsentService(project -> {
						approvalPrompts.incrementAndGet();
						return true;
					});

			assertTrue(approvingService.confirm(getFixture().getProject()));
			assertTrue(approvingService.confirm(getFixture().getProject()));
			assertEquals(1, approvalPrompts.get());
		} finally {
			properties.unsetValue(JaideTestGenerationConsentService.CONSENT_KEY);
		}
	}
}
