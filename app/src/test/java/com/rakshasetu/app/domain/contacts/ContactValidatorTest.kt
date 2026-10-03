package com.rakshasetu.app.domain.contacts

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactValidatorTest {

    @Test
    fun `name is required`() {
        val r = ContactValidator.validate("", "9876543210", "+91", "Friend", null)
        assertFalse(r.isValid)
    }

    @Test
    fun `short phone number is rejected`() {
        assertFalse(ContactValidator.validate("Mum", "12", "+91", "Parent", null).isValid)
    }

    @Test
    fun `ten digit Indian number is accepted with default country code`() {
        assertTrue(ContactValidator.validate("Mum", "9876543210", "+91", "Parent", null).isValid)
    }

    @Test
    fun `country code must start with plus`() {
        assertFalse(ContactValidator.validate("Mum", "9876543210", "91", "Parent", null).isValid)
    }

    @Test
    fun `relation Other requires custom relation`() {
        val r = ContactValidator.validate("Raj", "9876543210", "+91", "Other", "")
        assertFalse(r.isValid)
        assertTrue(ContactValidator.validate("Raj", "9876543210", "+91", "Other", "Neighbour").isValid)
    }
}
