package com.rakshasetu.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsVerificationHelperFormatTest {

    @Test
    fun `network estimate is labelled in emergency sms`() {
        val msg = SmsVerificationHelper.formatEmergencyMessage(
            template = "LOC {location} BAT {battery} DATA {data_status} T {timestamp}",
            latitude = 12.97, longitude = 77.59, accuracy = 800f,
            batteryLevel = 15, hasData = false, isBatteryLow = true, provider = "network"
        )
        assertTrue(msg.contains("accurate to ~800m"))
        assertTrue(msg.contains("network estimate"))
    }

    @Test
    fun `gps fix is not called network estimate`() {
        val msg = SmsVerificationHelper.formatEmergencyMessage(
            template = "{location}",
            latitude = 12.97, longitude = 77.59, accuracy = 15f,
            batteryLevel = 80, hasData = true, provider = "gps"
        )
        assertFalse(msg.contains("network estimate"))
    }

    @Test
    fun `missing location says unavailable`() {
        val msg = SmsVerificationHelper.formatEmergencyMessage(
            template = "{location}",
            latitude = null, longitude = null, accuracy = null,
            batteryLevel = null, hasData = true
        )
        assertTrue(msg.contains("Location unavailable"))
    }
}
