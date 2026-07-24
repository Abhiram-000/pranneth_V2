package com.rakshasetu.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rakshasetu.app.data.dao.AlertLogDao
import com.rakshasetu.app.data.dao.EmergencyContactDao
import com.rakshasetu.app.data.dao.LocationUpdateDao
import com.rakshasetu.app.data.entity.AlertLog
import com.rakshasetu.app.data.entity.EmergencyContact
import com.rakshasetu.app.data.entity.LocationUpdate

@Database(
    entities = [
        EmergencyContact::class,
        AlertLog::class,
        LocationUpdate::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RakshaSetuDatabase : RoomDatabase() {
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun alertLogDao(): AlertLogDao
    abstract fun locationUpdateDao(): LocationUpdateDao
}
