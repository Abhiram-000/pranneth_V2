package com.rakshasetu.app.domain.trigger

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ShakeDetectorTest {

    private class RecordingListener : ShakeDetector.ShakeListener {
        var detectedCounts = mutableListOf<Int>()
        var invalidCount = 0
        override fun onShakeDetected(eventCount: Int) { detectedCounts.add(eventCount) }
        override fun onShakePatternInvalid() { invalidCount++ }
    }

    private lateinit var listener: RecordingListener
    private lateinit var detector: ShakeDetector

    @Before
    fun setUp() {
        listener = RecordingListener()
        detector = ShakeDetector(listener, ShakeDetector.ShakeConfig(threshold = 8f, eventCount = 3, windowMs = 1500L))
    }

    // A strong kick away from baseline = one shake "event".
    private fun strongSample(t: Long) {
        detector.processSample(10f, 10f, 9.8f, t)
        detector.processSample(0f, 0f, 9.8f, t + 20)
    }

    @Test
    fun `three jolts inside window triggers detection`() {
        // Irregular spacing (CV of intervals ~0.55) — genuine shake, not cadence.
        strongSample(0)
        strongSample(700)
        strongSample(900)
        assertEquals(listOf(3), listener.detectedCounts)
    }

    @Test
    fun `two jolts do not trigger detection`() {
        strongSample(0)
        strongSample(700)
        assertTrue(listener.detectedCounts.isEmpty())
    }

    @Test
    fun `rhythmic periodic spikes are rejected as pattern invalid`() {
        for (i in 0..5) {
            detector.processSample(10f, 10f, 9.8f, i * 400L)
            detector.processSample(0f, 0f, 9.8f, i * 400L + 20)
        }
        assertTrue(listener.detectedCounts.isEmpty())
        assertTrue(listener.invalidCount > 0)
    }

    @Test
    fun `single axis jolts are ignored`() {
        repeat(3) { i ->
            detector.processSample(0f, 0f, 9.8f, i * 400L)
            detector.processSample(8f, 0f, 9.8f, i * 400L + 40)
        }
        assertTrue(listener.detectedCounts.isEmpty())
    }
}
