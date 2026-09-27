package com.antonstrokov.jaide.plugin.service;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

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

	@Test
	void shouldAskAgainAfterApprovalIsRevoked() {
		PropertiesComponent properties =
				PropertiesComponent.getInstance(getFixture().getProject());

		properties.unsetValue(JaideTestGenerationConsentService.CONSENT_KEY);

		try {
			AtomicInteger prompts = new AtomicInteger();
			JaideTestGenerationConsentService service =
					new JaideTestGenerationConsentService(project -> {
						prompts.incrementAndGet();
						return true;
					});

			assertTrue(service.confirm(getFixture().getProject()));
			assertEquals(1, prompts.get());

			service.revoke(getFixture().getProject());
			assertNull(properties.getValue(JaideTestGenerationConsentService.CONSENT_KEY));

			assertTrue(service.confirm(getFixture().getProject()));
			assertEquals(2, prompts.get());
		} finally {
			properties.unsetValue(JaideTestGenerationConsentService.CONSENT_KEY);
		}
	}
}
