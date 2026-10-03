package com.rakshasetu.app.worker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WatchdogPolicyTest {

    @Test
    fun `restarts when service not running and monitoring expected on`() {
        assertTrue(WatchdogPolicy.shouldRestart(isRunning = false, monitoringEnabled = true))
    }

    @Test
    fun `does not restart when monitoring intentionally off`() {
        assertFalse(WatchdogPolicy.shouldRestart(isRunning = false, monitoringEnabled = false))
    }

    @Test
    fun `does not report dead when running`() {
        assertFalse(WatchdogPolicy.shouldRestart(isRunning = true, monitoringEnabled = true))
    }

    @Test
    fun `stale heartbeat means sensors likely dead even if service object exists`() {
        assertTrue(WatchdogPolicy.heartbeatStale(nowMs = 1_000_000L, lastPingMs = 900_000L, maxAgeMs = 60_000L))
        assertFalse(WatchdogPolicy.heartbeatStale(nowMs = 1_000_000L, lastPingMs = 970_000L, maxAgeMs = 60_000L))
    }
}
