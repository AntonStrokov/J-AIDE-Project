package com.antonstrokov.jaide.plugin.service;

import com.antonstrokov.jaide.plugin.dto.health.JaideHealthResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaideAiSetupCheckServiceTest {

	private final JaideAiSetupCheckService service =
			new JaideAiSetupCheckService();

	@Test
	void shouldNotReportResultConsumerFailureAsBackendFailure() {
		JaideHealthResponse response = new JaideHealthResponse(
				null, null, null, null, 0L, "Test response"
		);
		IllegalStateException uiFailure =
				new IllegalStateException("Preview update failed");
		AtomicInteger backendErrorCount = new AtomicInteger();

		IllegalStateException thrown = assertThrows(
				IllegalStateException.class,
				() -> service.runCheck(
						() -> response,
						ignored -> {
							throw uiFailure;
						},
						ignored -> backendErrorCount.incrementAndGet()
				)
		);

		assertSame(uiFailure, thrown);
		assertEquals(0, backendErrorCount.get());
	}

	@Test
	void shouldReportBackendFailureWithoutUpdatingResult() {
		AtomicInteger resultCount = new AtomicInteger();
		List<String> errorMessages = new ArrayList<>();

		service.runCheck(
				() -> {
					throw new IOException("Connection refused");
				},
				ignored -> resultCount.incrementAndGet(),
				errorMessages::add
		);

		assertEquals(0, resultCount.get());
		assertEquals(1, errorMessages.size());
		assertTrue(errorMessages.getFirst().contains("backend is not available"));
	}
}
