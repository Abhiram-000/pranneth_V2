package com.rakshasetu.app.hilt

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.rakshasetu.app.data.RakshaSetuDatabase
import com.rakshasetu.app.data.dao.AlertLogDao
import com.rakshasetu.app.data.dao.EmergencyContactDao
import com.rakshasetu.app.data.dao.LocationUpdateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("rakshasetu_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RakshaSetuDatabase {
        return Room.databaseBuilder(
            context,
            RakshaSetuDatabase::class.java,
            "rakshasetu.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideEmergencyContactDao(db: RakshaSetuDatabase): EmergencyContactDao {
        return db.emergencyContactDao()
    }

    @Provides
    fun provideAlertLogDao(db: RakshaSetuDatabase): AlertLogDao {
        return db.alertLogDao()
    }

    @Provides
    fun provideLocationUpdateDao(db: RakshaSetuDatabase): LocationUpdateDao {
        return db.locationUpdateDao()
    }
}
