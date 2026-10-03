package com.rakshasetu.app.domain.location

/** Scheduling rule for an active alert's location updates. */
object LocationTrackingPolicy {

    /** Tick times (ms since alert start) at which an update should be emitted. */
    fun tickTimes(intervalMs: Long, durationMs: Long): List<Long> {
        if (intervalMs <= 0L || durationMs <= 0L) return emptyList()
        val ticks = mutableListOf<Long>()
        var t = intervalMs
        while (t <= durationMs) {
            ticks.add(t)
            t += intervalMs
        }
        return ticks
    }
}
