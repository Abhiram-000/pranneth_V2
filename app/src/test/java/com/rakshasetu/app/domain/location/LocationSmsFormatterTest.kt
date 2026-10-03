package com.rakshasetu.app.domain.location

import org.junit.Assert.assertTrue
import org.junit.Test

class LocationSmsFormatterTest {

    @Test
    fun `null location reports unavailable`() {
        assertTrue(LocationSmsFormatter.format(null, null, null, null).contains("unavailable", ignoreCase = true))
    }

    @Test
    fun `gps fix includes coordinates and accuracy`() {
        val s = LocationSmsFormatter.format(12.97, 77.59, 15f, "gps")
        assertTrue(s.contains("12.97"))
        assertTrue(s.contains("77.59"))
        assertTrue(s.contains("~15m"))
    }

    @Test
    fun `network fix is labelled as estimate`() {
        val s = LocationSmsFormatter.format(12.97, 77.59, 800f, "network")
        assertTrue(s.contains("accurate to ~800m"))
        assertTrue(s.contains("network", ignoreCase = true))
    }

    @Test
    fun `gps fix is not labelled network`() {
        val s = LocationSmsFormatter.format(1.0, 2.0, 20f, "gps")
        assertTrue(!s.contains("network", ignoreCase = true))
    }
}
