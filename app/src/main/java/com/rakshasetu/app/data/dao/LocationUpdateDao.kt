package com.rakshasetu.app.data.dao

import androidx.room.*
import com.rakshasetu.app.data.entity.LocationUpdate

@Dao
interface LocationUpdateDao {

    @Insert
    suspend fun insertLocation(update: LocationUpdate): Long

    @Query("SELECT * FROM location_updates WHERE alertId = :alertId ORDER BY timestamp ASC")
    suspend fun getLocationsForAlert(alertId: Long): List<LocationUpdate>

    @Query("SELECT * FROM location_updates WHERE alertId = :alertId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLocationForAlert(alertId: Long): LocationUpdate?

    @Query("DELETE FROM location_updates WHERE alertId = :alertId")
    suspend fun deleteLocationsForAlert(alertId: Long)

    @Query("DELETE FROM location_updates WHERE timestamp < :before")
    suspend fun deleteLocationsOlderThan(before: Long)
}
