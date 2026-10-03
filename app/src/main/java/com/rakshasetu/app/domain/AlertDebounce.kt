package com.rakshasetu.app.domain

/** Pure re-trigger debounce: a malfunctioning sensor cannot spam contacts. */
object AlertDebounce {

    fun shouldAllow(lastAlertMs: Long, nowMs: Long, minGapMs: Long): Boolean =
        lastAlertMs == 0L || nowMs - lastAlertMs >= minGapMs
}
