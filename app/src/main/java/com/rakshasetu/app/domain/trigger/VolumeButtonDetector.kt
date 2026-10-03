package com.rakshasetu.app.domain.trigger

/**
 * Detects volume button combo patterns (Volume Up + Down together or triple-press)
 * via AccessibilityService events. Works from lock screen.
 */
class VolumeButtonDetector(
    private val listener: VolumeComboListener,
    private val config: VolumeConfig = VolumeConfig()
) {
    data class VolumeConfig(
        val requiredPresses: Int = 2,
        val windowMs: Long = 300L,
        val requireBothButtons: Boolean = true
    )

    interface VolumeComboListener {
        fun onVolumeComboDetected()
    }

    private val pressTimestamps = mutableListOf<Long>()
    private var upPressed = false
    private var downPressed = false
    private var lastUpTime = 0L
    private var lastDownTime = 0L
    private var lastFiredAt = -REARM_COOLDOWN_MS

    @Volatile
    private var config_copy = config

    fun updateConfig(config: VolumeConfig) {
        this.config_copy = config
    }

    /**
     * Called by AccessibilityService when a volume key event occurs.
     * @param keyCode KeyEvent.KEYCODE_VOLUME_UP or KEYCODE_VOLUME_DOWN
     * @param isDown true if key pressed, false if released
     */
    fun onKeyEvent(keyCode: Int, isDown: Boolean) {
        val currentTime = System.currentTimeMillis()

        when (keyCode) {
            24 -> { // VOLUME_UP
                if (isDown) {
                    upPressed = true
                    lastUpTime = currentTime
                } else {
                    upPressed = false
                }
            }
            25 -> { // VOLUME_DOWN
                if (isDown) {
                    downPressed = true
                    lastDownTime = currentTime
                } else {
                    downPressed = false
                }
            }
        }

        // Enforced cooldown: one detection suppresses all re-fires for 500 ms.
        if (currentTime - lastFiredAt < REARM_COOLDOWN_MS) return

        // Check if both buttons pressed within the time window
        if (config_copy.requireBothButtons) {
            if (upPressed && downPressed) {
                val timeDiff = kotlin.math.abs(lastUpTime - lastDownTime)
                if (timeDiff <= config_copy.windowMs) {
                    listener.onVolumeComboDetected()
                    lastFiredAt = currentTime
                    pressTimestamps.clear()
                    upPressed = false
                    downPressed = false
                    return
                }
            }
        }

        // Alternative: count presses within window
        if (!config_copy.requireBothButtons) {
            if (isDown && (keyCode == 24 || keyCode == 25)) {
                pressTimestamps.add(currentTime)
                pressTimestamps.removeAll { currentTime - it > config_copy.windowMs }

                if (pressTimestamps.size >= config_copy.requiredPresses) {
                    listener.onVolumeComboDetected()
                    lastFiredAt = currentTime
                    pressTimestamps.clear()
                }
            }
        }
    }

    fun reset() {
        pressTimestamps.clear()
        upPressed = false
        downPressed = false
        lastUpTime = 0L
        lastDownTime = 0L
        lastFiredAt = -REARM_COOLDOWN_MS
    }

    companion object {
        const val REARM_COOLDOWN_MS = 500L
    }
}
