package com.rakshasetu.app.worker

/** Pure watchdog decisions, unit-tested without Android. */
object WatchdogPolicy {

    fun shouldRestart(isRunning: Boolean, monitoringEnabled: Boolean): Boolean =
        !isRunning && monitoringEnabled

    fun heartbeatStale(nowMs: Long, lastPingMs: Long, maxAgeMs: Long): Boolean =
        nowMs - lastPingMs > maxAgeMs
}
