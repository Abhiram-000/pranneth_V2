package com.rakshasetu.app.ui.countdown

/**
 * Decides how the countdown should present, from the silent-countdown setting.
 * The silent path must not vibrate, flash, or ring — only dim and quietly count.
 */
object CountdownPolicy {

    data class Behavior(
        val vibrate: Boolean,
        val flashScreen: Boolean,
        val dimScreen: Boolean,
        val showCountdownUi: Boolean
    )

    fun behavior(isSilent: Boolean): Behavior = if (isSilent) {
        Behavior(vibrate = false, flashScreen = false, dimScreen = true, showCountdownUi = true)
    } else {
        Behavior(vibrate = true, flashScreen = true, dimScreen = false, showCountdownUi = true)
    }
}
