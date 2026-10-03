package com.rakshasetu.app.domain.location

/**
 * Pure SMS location formatting: always honest about accuracy and source,
 * so contacts never read a precise-looking pin for a coarse network fix.
 */
object LocationSmsFormatter {

    fun format(latitude: Double?, longitude: Double?, accuracy: Float?, provider: String?): String {
        if (latitude == null || longitude == null) return "Location unavailable"
        val accuracyText = accuracy?.let { " (accurate to ~${it.toInt()}m)" } ?: ""
        val sourceText = if (provider == "network") " — network estimate" else ""
        return "$latitude,$longitude$accuracyText$sourceText"
    }
}
