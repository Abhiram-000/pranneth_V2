package com.rakshasetu.app.domain.trigger

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Detects shake gestures using the accelerometer with false-positive defenses:
 * - Multi-axis jolt pattern (not just magnitude)
 * - Non-rhythmic check (filters running cadence)
 * - Configurable sensitivity
 * - Rolling window for multiple events
 * - Fast sensor rate for screen-off reliability
 */
class ShakeDetector(
    private var listener: ShakeListener,
    private var config: ShakeConfig = ShakeConfig()
) : SensorEventListener {

    data class ShakeConfig(
        val threshold: Float = 8f,          // Lowered from 12 for sensitivity
        val eventCount: Int = 3,            // 3 shakes in window
        val windowMs: Long = 1500L,         // Tightened from 2000
        val sensitivityMultiplier: Float = 1.0f
    ) {
        val effectiveThreshold: Float get() = threshold * sensitivityMultiplier
    }

    interface ShakeListener {
        fun onShakeDetected(eventCount: Int)
        fun onShakePatternInvalid()
    }

    companion object {
        private const val MIN_PEAK_GAP_MS = 80L
    }

    private var lastShakeTimestamp = 0L
    private val shakeTimestamps = mutableListOf<Long>()
    private var lastAcceleration = floatArrayOf(0f, 0f, 0f)
    private var lastMagnitude = 0f
    private var isListening = false

    private var rhythmSuspect = false
    private var suspectIntervalMs = 0L

    // Periodicity detection for filtering rhythmic movement (running)
    private val recentIntervals = mutableListOf<Long>()
    private var lastPeakTimestamp = -1L

    fun start(sensorManager: SensorManager) {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            // Low-power batched sampling: GAME rate still resolves shake bursts
            // (tens of ms) while letting the sensor FIFO wake the CPU up to every
            // 100 ms instead of every UI frame (~60 Hz). ~3-6x lower idle drain.
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME,
                100_000 // 100 ms max batch latency
            )
            isListening = true
        }
    }

    fun stop(sensorManager: SensorManager) {
        sensorManager.unregisterListener(this)
        isListening = false
        reset()
    }

    fun updateConfig(newConfig: ShakeConfig) {
        this.config = newConfig
    }

    fun reset() {
        shakeTimestamps.clear()
        recentIntervals.clear()
        rhythmSuspect = false
        suspectIntervalMs = 0L
        lastPeakTimestamp = -1L
        lastAcceleration = floatArrayOf(0f, 0f, 0f)
        lastMagnitude = 0f
    }

    fun isListening() = isListening

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
        processSample(event.values[0], event.values[1], event.values[2], System.currentTimeMillis())
    }

    /**
     * Pure signal-processing core, separated from the Android sensor callback
     * so the false-positive defenses are unit-testable without a SensorEvent.
     */
    fun processSample(x: Float, y: Float, z: Float, currentTime: Long) {

        // Calculate acceleration change (not absolute value — subtracts gravity)
        val deltaX = abs(x - lastAcceleration[0])
        val deltaY = abs(y - lastAcceleration[1])
        val deltaZ = abs(z - lastAcceleration[2])

        lastAcceleration = floatArrayOf(x, y, z)

        // FALSE-POSITIVE DEFENSE 1: Multi-axis check
        // Require significant change across at least 2 axes (not just one big jolt)
        val axesWithSignificantChange = listOf(deltaX, deltaY, deltaZ).count {
            it > config.effectiveThreshold * 0.4f
        }
        if (axesWithSignificantChange < 2) return

        // Calculate magnitude of acceleration change
        val magnitude = sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)

        // FALSE-POSITIVE DEFENSE 2: Threshold check
        if (magnitude < config.effectiveThreshold) return

        // Ignore micro-peaks from the same physical jolt (e.g. a kick and its immediate return).
        if (lastPeakTimestamp >= 0 && currentTime - lastPeakTimestamp < MIN_PEAK_GAP_MS) return

        // FALSE-POSITIVE DEFENSE 3: Periodicity check (filters running/cycling cadence)
        if (lastPeakTimestamp >= 0) {
            val interval = currentTime - lastPeakTimestamp
            recentIntervals.add(interval)
            if (recentIntervals.size > 5) recentIntervals.removeFirst()

            // If intervals are regular (low variance), it's rhythmic — ignore
            if (recentIntervals.size >= 2 && isRhythmic(recentIntervals)) {
                // Sticky veto: keep suppressing peaks whose intervals match the
                // rhythmic period (alternating-interval cadences still match the
                // median period) until the pattern clearly breaks.
                rhythmSuspect = true
                suspectIntervalMs = recentIntervals.sorted()[recentIntervals.size / 2]
                shakeTimestamps.clear()
                lastPeakTimestamp = currentTime
                listener.onShakePatternInvalid()
                return
            }
        }

        if (rhythmSuspect) {
            val interval = currentTime - lastPeakTimestamp
            if (kotlin.math.abs(interval - suspectIntervalMs) <= suspectIntervalMs / 2) {
                lastPeakTimestamp = currentTime
                return // still rhythmic — stay silent
            }
            rhythmSuspect = false // cadence broke: resume genuine-shake detection
            recentIntervals.clear()
        }
        lastPeakTimestamp = currentTime

        // Add to rolling window
        shakeTimestamps.add(currentTime)

        // Remove old timestamps outside the window
        shakeTimestamps.removeAll { currentTime - it > config.windowMs }

        // Check if we have enough events in the window
        if (shakeTimestamps.size >= config.eventCount) {
            listener.onShakeDetected(shakeTimestamps.size)
            shakeTimestamps.clear()
            recentIntervals.clear()
        }

        lastMagnitude = magnitude
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * Checks if intervals are too regular (rhythmic movement like running).
     * Uses coefficient of variation — low CV = regular rhythm = false positive.
     */
    private fun isRhythmic(intervals: List<Long>): Boolean {
        if (intervals.size < 2) return false
        val mean = intervals.average()
        if (mean == 0.0) return false
        val variance = intervals.map { (it - mean) * (it - mean) }.average()
        val stdDev = sqrt(variance)
        val coefficientOfVariation = stdDev / mean

        // Running cadence typically has CV < 0.15 (very regular)
        // Genuine shake has CV > 0.3 (irregular)
        return coefficientOfVariation < 0.15
    }
}
