package com.example.healthapp

import android.content.Context
import androidx.room.Room
import com.example.healthapp.database.bpm.daily.BpmDaily
import com.example.healthapp.database.bpm.daily.BpmDailyDao
import com.example.healthapp.database.bpm.daily.BpmDailyDatabase
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourly
import com.example.healthapp.database.bpm.hourly.BpmHourlyDao
import com.example.healthapp.database.bpm.hourly.BpmHourlyDatabase
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.bpm.last.BpmDao
import com.example.healthapp.database.bpm.last.BpmDatabase
import com.example.healthapp.database.bpm.last.BpmRepository
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
    fun provideBpmDao(database: BpmDatabase): BpmDao {
        return database.bpmDao()
    }

    @Provides
    @Singleton
    fun provideBpmRepository(bpmDao: BpmDao): BpmRepository {
        return BpmRepository(bpmDao)
    }

    @Provides
    @Singleton
    fun provideBpmDatabase(@ApplicationContext appContext: Context): BpmDatabase {
        return Room.databaseBuilder(
            appContext,
            BpmDatabase::class.java,
            "health_app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBpmHourlyDao(database: BpmHourlyDatabase): BpmHourlyDao {
        return database.bpmHourlyDao()
    }

    @Provides
    @Singleton
    fun provideBpmHourlyRepository(bpmHourlyDao: BpmHourlyDao): BpmHourlyRepository {
        return BpmHourlyRepository(bpmHourlyDao)
    }

    @Provides
    @Singleton
    fun provideBpmHourlyDatabase(@ApplicationContext appContext: Context): BpmHourlyDatabase {
        return Room.databaseBuilder(
            appContext,
            BpmHourlyDatabase::class.java,
            "bpm_hourly_database"
        ).build()
    }


    @Provides
    @Singleton
    fun provideBpmDailyDao(database: BpmDailyDatabase): BpmDailyDao {
        return database.bpmDailyDao()
    }

    @Provides
    @Singleton
    fun provideBpmDailyRepository(bpmDailyDao: BpmDailyDao): BpmDailyRepository {
        return BpmDailyRepository(bpmDailyDao)
    }

    @Provides
    @Singleton
    fun provideBpmDailyDatabase(@ApplicationContext appContext: Context): BpmDailyDatabase {
        return Room.databaseBuilder(
            appContext,
            BpmDailyDatabase::class.java,
            "bpm_daily_database"
        ).build()
    }

}