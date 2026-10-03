package com.rakshasetu.app.service

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TriggerLauncherPolicyTest {

    @Test
    fun `no exception means activity started — no fallback`() {
        assertFalse(TriggerLauncher.fallbackNeeded(null, 34))
    }

    @Test
    fun `background-start restriction on API 31+ needs full-screen fallback`() {
        assertTrue(TriggerLauncher.fallbackNeeded(SecurityException("Background start not allowed"), 34))
    }

    @Test
    fun `any activity-launch failure needs fallback notification`() {
        assertTrue(TriggerLauncher.fallbackNeeded(IllegalStateException("from background"), 28))
    }
}
