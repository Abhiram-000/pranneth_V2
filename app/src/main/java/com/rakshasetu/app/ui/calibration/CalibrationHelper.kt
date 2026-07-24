package com.rakshasetu.app.ui.calibration

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Guided calibration for shake detection.
 * User practices the shake gesture and the system measures the intensity
 * to set optimal thresholds for their specific phone and grip.
 */
class CalibrationHelper(
    private val context: Context,
    private val listener: CalibrationListener
) : SensorEventListener {

    interface CalibrationListener {
        fun onCalibrationStart()
        fun onShakeDetected(magnitude: Float, peakCount: Int)
        fun onCalibrationComplete(recommendedThreshold: Float, recommendedSensitivity: String)
        fun onCalibrationError(error: String)
    }

    private var sensorManager: SensorManager? = null
    private var isCalibrating = false
    private val peakMagnitudes = mutableListOf<Float>()
    private val shakeTimestamps = mutableListOf<Long>()
    private var lastAcceleration = floatArrayOf(0f, 0f, 0f)
    private var totalShakes = 0

    // Calibration parameters
    private val calibrationDurationMs = 10_000L // 10 seconds
    private val minShakesRequired = 3
    private var calibrationStartTime = 0L

    fun startCalibration() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            listener.onCalibrationError("Accelerometer not available on this device")
            return
        }

        peakMagnitudes.clear()
        shakeTimestamps.clear()
        totalShakes = 0
        isCalibrating = true
        calibrationStartTime = System.currentTimeMillis()

        sensorManager?.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME,
            50_000 // 50ms max report latency for accurate measurement
        )

        listener.onCalibrationStart()
    }

    fun stopCalibration() {
        isCalibrating = false
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!isCalibrating || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val currentTime = System.currentTimeMillis()
        val elapsed = currentTime - calibrationStartTime

        if (elapsed > calibrationDurationMs) {
            completeCalibration()
            return
        }

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val deltaX = kotlin.math.abs(x - lastAcceleration[0])
        val deltaY = kotlin.math.abs(y - lastAcceleration[1])
        val deltaZ = kotlin.math.abs(z - lastAcceleration[2])

        lastAcceleration = floatArrayOf(x, y, z)

        val magnitude = sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)

        // Detect shake (use a generous threshold for calibration)
        if (magnitude > 8f) {
            // Check if this is a new shake (not continuous vibration)
            if (shakeTimestamps.isEmpty() || currentTime - shakeTimestamps.last() > 200) {
                shakeTimestamps.add(currentTime)
                peakMagnitudes.add(magnitude)
                totalShakes++

                // Remove old timestamps
                shakeTimestamps.removeAll { currentTime - it > calibrationDurationMs }

                listener.onShakeDetected(magnitude, totalShakes)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun completeCalibration() {
        stopCalibration()

        if (peakMagnitudes.isEmpty()) {
            listener.onCalibrationError("No shake gestures detected. Try shaking more vigorously.")
            return
        }

        if (totalShakes < minShakesRequired) {
            listener.onCalibrationError(
                "Only $totalShakes shake(s) detected. We need at least $minShakesRequired. " +
                "Try shaking harder and faster."
            )
            return
        }

        // Calculate recommended threshold based on measured peaks
        val avgPeak = peakMagnitudes.average().toFloat()
        val minPeak = peakMagnitudes.min()
        val maxPeak = peakMagnitudes.max()

        // Set threshold at 60% of average peak for balanced sensitivity
        val recommendedThreshold = avgPeak * 0.6f

        // Determine sensitivity category
        val recommendedSensitivity = when {
            avgPeak < 15f -> "HIGH"   // Weak shakes — needs high sensitivity
            avgPeak < 25f -> "MEDIUM" // Normal shakes
            else -> "LOW"             // Strong shakes — can use low sensitivity
        }

        listener.onCalibrationComplete(recommendedThreshold, recommendedSensitivity)
    }

    /**
     * Returns calibration data for storage.
     */
    data class CalibrationResult(
        val threshold: Float,
        val sensitivity: String,
        val avgPeak: Float,
        val shakeCount: Int
    )
}
