package com.rakshasetu.app.domain.sms

import org.junit.Assert.assertEquals
import org.junit.Test

class SmsRetrySchedulerTest {

    @Test
    fun `retry plan is exponential backoff 10s 30s 90s`() {
        assertEquals(listOf(10_000L, 30_000L, 90_000L), SmsRetryScheduler.plan())
    }

    @Test
    fun `after all primary retries fail switch to secondary sim`() {
        assertEquals(0, SmsRetryScheduler.subscriptionIndexForAttempt(0, hasDualSim = true))
        assertEquals(0, SmsRetryScheduler.subscriptionIndexForAttempt(1, hasDualSim = true))
        assertEquals(0, SmsRetryScheduler.subscriptionIndexForAttempt(2, hasDualSim = true))
        assertEquals(1, SmsRetryScheduler.subscriptionIndexForAttempt(3, hasDualSim = true))
    }

    @Test
    fun `single sim devices stay on primary`() {
        assertEquals(0, SmsRetryScheduler.subscriptionIndexForAttempt(5, hasDualSim = false))
    }
}
