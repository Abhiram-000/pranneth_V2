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
 */
class ShakeDetector(
    private val listener: ShakeListener,
    private val config: ShakeConfig = ShakeConfig()
) : SensorEventListener {

    data class ShakeConfig(
        val threshold: Float = 12f,
        val eventCount: Int = 3,
        val windowMs: Long = 2000L,
        val sensitivityMultiplier: Float = 1.0f
    ) {
        val effectiveThreshold: Float get() = threshold * sensitivityMultiplier
    }

    interface ShakeListener {
        fun onShakeDetected(eventCount: Int)
        fun onShakePatternInvalid()
    }

    private var lastShakeTimestamp = 0L
    private val shakeTimestamps = mutableListOf<Long>()
    private var lastAcceleration = floatArrayOf(0f, 0f, 0f)
    private var lastMagnitude = 0f
    private var isListening = false

    // Periodicity detection for filtering rhythmic movement (running)
    private val recentIntervals = mutableListOf<Long>()
    private var lastPeakTimestamp = 0L

    fun start(sensorManager: SensorManager) {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            // Use batched low-power mode for battery efficiency
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                100_000 // 100ms max report latency
            )
            isListening = true
        }
    }

    fun stop(sensorManager: SensorManager) {
        sensorManager.unregisterListener(this)
        isListening = false
        reset()
    }

    fun updateConfig(config: ShakeConfig) {
        this.config_copy = config
    }

    @Volatile
    private var config_copy = config

    fun reset() {
        shakeTimestamps.clear()
        recentIntervals.clear()
        lastPeakTimestamp = 0L
        lastAcceleration = floatArrayOf(0f, 0f, 0f)
        lastMagnitude = 0f
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val currentTime = System.currentTimeMillis()
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calculate acceleration change (not absolute value — subtracts gravity)
        val deltaX = abs(x - lastAcceleration[0])
        val deltaY = abs(y - lastAcceleration[1])
        val deltaZ = abs(z - lastAcceleration[2])

        lastAcceleration = floatArrayOf(x, y, z)

        // FALSE-POSITIVE DEFENSE 1: Multi-axis check
        // Require significant change across at least 2 axes (not just one big jolt)
        val axesWithSignificantChange = listOf(deltaX, deltaY, deltaZ).count {
            it > config_copy.effectiveThreshold * 0.5f
        }
        if (axesWithSignificantChange < 2) return

        // Calculate magnitude of acceleration change
        val magnitude = sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)

        // FALSE-POSITIVE DEFENSE 2: Threshold check
        if (magnitude < config_copy.effectiveThreshold) return

        // FALSE-POSITIVE DEFENSE 3: Periodicity check (filters running/cycling cadence)
        if (lastPeakTimestamp > 0) {
            val interval = currentTime - lastPeakTimestamp
            recentIntervals.add(interval)
            if (recentIntervals.size > 5) recentIntervals.removeFirst()

            // If intervals are regular (low variance), it's rhythmic — ignore
            if (recentIntervals.size >= 3 && isRhythmic(recentIntervals)) {
                listener.onShakePatternInvalid()
                return
            }
        }
        lastPeakTimestamp = currentTime

        // Add to rolling window
        shakeTimestamps.add(currentTime)

        // Remove old timestamps outside the window
        shakeTimestamps.removeAll { currentTime - it > config_copy.windowMs }

        // Check if we have enough events in the window
        if (shakeTimestamps.size >= config_copy.eventCount) {
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
        if (intervals.size < 3) return false
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
