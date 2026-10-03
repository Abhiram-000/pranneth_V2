package com.rakshasetu.app.domain.location

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationTrackingPolicyTest {

    @Test
    fun `tick times respect interval and stop at duration cap`() {
        val ticks = LocationTrackingPolicy.tickTimes(intervalMs = 120_000L, durationMs = 1_800_000L)
        assertEquals(120_000L, ticks.first())
        assertEquals(1_800_000L, ticks.last())
        assertEquals(15, ticks.size)
    }

    @Test
    fun `never fires past the duration cap`() {
        val ticks = LocationTrackingPolicy.tickTimes(120_000L, 1_800_000L)
        assertTrue(ticks.all { it <= 1_800_000L })
        assertFalse(ticks.contains(1_800_001L))
    }

    @Test
    fun `zero interval yields no ticks`() {
        assertEquals(emptyList<Long>(), LocationTrackingPolicy.tickTimes(0L, 1_800_000L))
    }
}
