package com.rakshasetu.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_logs")
data class AlertLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val triggerType: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracy: Float? = null,
    val locationSource: String? = null,
    val smsSentCount: Int = 0,
    val callsMadeCount: Int = 0,
    val isEscalated: Boolean = false,
    val isCancelled: Boolean = false,
    val cancelMethod: String? = null,
    val isDuress: Boolean = false,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val batteryLevel: Int? = null,
    val hasDataConnection: Boolean = true
) {
    companion object {
        const val TRIGGER_SHAKE = "shake"
        const val TRIGGER_VOLUME = "volume"
        const val TRIGGER_MANUAL = "manual"
        const val TRIGGER_NOTIFICATION = "notification"
        const val TRIGGER_AIRPLANE = "airplane_mode"
    }
}
