package com.rakshasetu.app.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertDebounceTest {

    @Test
    fun `first alert allowed`() {
        assertTrue(AlertDebounce.shouldAllow(lastAlertMs = 0L, nowMs = 100_000L, minGapMs = 60_000L))
    }

    @Test
    fun `alert within min gap blocked`() {
        assertFalse(AlertDebounce.shouldAllow(lastAlertMs = 50_000L, nowMs = 100_000L, minGapMs = 60_000L))
    }

    @Test
    fun `alert after gap allowed`() {
        assertTrue(AlertDebounce.shouldAllow(lastAlertMs = 30_000L, nowMs = 100_000L, minGapMs = 60_000L))
    }
}
