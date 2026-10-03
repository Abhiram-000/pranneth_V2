package com.rakshasetu.app.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OEMHelperTest {

    @Test
    fun `xiaomi gets autostart instructions`() {
        val r = OEMHelper.instructionsFor("Xiaomi")
        assertTrue(r.hasAutoStartSetting)
        assertTrue(r.steps.isNotEmpty())
    }

    @Test
    fun `redmi maps to xiaomi guide`() {
        assertTrue(OEMHelper.instructionsFor("redmi").manufacturer.contains("Xiaomi"))
    }

    @Test
    fun `samsung gets own guide`() {
        assertTrue(OEMHelper.instructionsFor("samsung").manufacturer.contains("Samsung"))
    }

    @Test
    fun `unknown oem gets generic guide instead of empty`() {
        val r = OEMHelper.instructionsFor("somebrand")
        assertTrue(r.steps.isNotEmpty())
        assertFalse(r.manufacturer.isBlank())
    }
}
