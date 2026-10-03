package com.rakshasetu.app.domain.sms

/**
 * Pure retry policy for emergency SMS: 10s → 30s → 90s backoff, then the
 * fourth (final) attempt flips to the secondary SIM on dual-SIM devices.
 * Multi-part/Unicode handling stays in SMSDispatcher.
 */
object SmsRetryScheduler {

    fun plan(): List<Long> = listOf(10_000L, 30_000L, 90_000L)

    /**
     * Attempt index (0-based) → which subscription slot to send on.
     * Primary for attempts 0–2, secondary (index 1) for attempt 3+ on dual-SIM.
     * Returns 0-based index into the active subscription list; callers map
     * 1 → "secondary" because subscription IDs are not 0/1 directly.
     */
    fun subscriptionIndexForAttempt(attempt: Int, hasDualSim: Boolean): Int {
        if (!hasDualSim) return 0
        return if (attempt >= 3) 1 else 0
    }
}
