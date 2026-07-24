package com.rakshasetu.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_updates")
data class LocationUpdate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alertId: Long,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float?,
    val source: String,
    val timestamp: Long = System.currentTimeMillis()
)
