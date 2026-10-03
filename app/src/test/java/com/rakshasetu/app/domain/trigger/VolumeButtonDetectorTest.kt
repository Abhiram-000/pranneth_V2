package com.rakshasetu.app.domain.trigger

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class VolumeButtonDetectorTest {

    private class RecordingListener : VolumeButtonDetector.VolumeComboListener {
        var fired = 0
        override fun onVolumeComboDetected() { fired++ }
    }

    private lateinit var listener: RecordingListener
    private lateinit var detector: VolumeButtonDetector

    @Before
    fun setUp() {
        listener = RecordingListener()
        detector = VolumeButtonDetector(
            listener,
            VolumeButtonDetector.VolumeConfig(requiredPresses = 2, windowMs = 300L, requireBothButtons = true)
        )
    }

    @Test
    fun `both buttons within window fires once`() {
        detector.onKeyEvent(24, true)   // UP down
        Thread.sleep(50)
        detector.onKeyEvent(25, true)   // DOWN down
        assertEquals(1, listener.fired)
    }

    @Test
    fun `holding both buttons does not re-fire`() {
        detector.onKeyEvent(24, true)
        detector.onKeyEvent(25, true)
        // more correlated DOWN events while both are held
        detector.onKeyEvent(25, true)
        detector.onKeyEvent(24, true)
        assertEquals(1, listener.fired)
    }

    @Test
    fun `keys too far apart do not fire`() {
        detector.onKeyEvent(24, true)
        detector.onKeyEvent(24, false)
        Thread.sleep(400)
        detector.onKeyEvent(25, true)
        assertEquals(0, listener.fired)
    }

    @Test
    fun `immediate re-arm is blocked by cooldown`() {
        detector.onKeyEvent(24, true)
        detector.onKeyEvent(25, true)
        assertEquals(1, listener.fired)
        detector.onKeyEvent(24, false)
        detector.onKeyEvent(25, false)
        // within the 500 ms re-arm window — a new press pair must not re-fire
        detector.onKeyEvent(24, true)
        detector.onKeyEvent(25, true)
        assertEquals(1, listener.fired)
    }
}
