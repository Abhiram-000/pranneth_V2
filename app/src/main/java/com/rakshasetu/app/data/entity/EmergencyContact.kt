package com.rakshasetu.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val countryCode: String = "+91",
    val relation: String,
    val customRelation: String? = null,
    var priority: Int = 0,
    val isVerified: Boolean = false,
    val lastNotifiedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val fullPhoneNumber: String
        get() = "$countryCode$phoneNumber"

    val displayRelation: String
        get() = if (relation == "Other" && !customRelation.isNullOrBlank()) customRelation else relation

    companion object {
        val RELATIONS = listOf(
            "Parent", "Sibling", "Spouse", "Friend", "Colleague", "Other"
        )
    }
}
