package com.rakshasetu.app.domain.contacts

/**
 * Entry-time validation so a typo never silently produces an un-dialable contact.
 */
object ContactValidator {

    data class ValidationResult(val isValid: Boolean, val error: String? = null)

    fun validate(
        name: String,
        phone: String,
        countryCode: String,
        relation: String,
        customRelation: String?
    ): ValidationResult {
        if (name.isBlank()) return ValidationResult(false, "Name is required")
        val digits = phone.filter { it.isDigit() }
        if (digits.length < 10) return ValidationResult(false, "Enter a valid phone number (>=10 digits)")
        if (!countryCode.startsWith("+") || countryCode.length < 2) {
            return ValidationResult(false, "Country code must start with +")
        }
        if (relation == "Other" && customRelation.isNullOrBlank()) {
            return ValidationResult(false, "Specify the relation")
        }
        return ValidationResult(true)
    }
}
