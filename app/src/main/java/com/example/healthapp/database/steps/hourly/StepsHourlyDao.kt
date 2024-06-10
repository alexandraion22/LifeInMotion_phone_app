package com.example.healthapp.database.steps.hourly

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StepsHourlyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stepsHourly: StepsHourly)

    @Query("SELECT * FROM steps_hourly_table WHERE timestamp == :startOfHour")
    suspend fun getEntryForHour(startOfHour: Long): StepsHourly?

    @Query("SELECT * FROM steps_hourly_table WHERE timestamp >= :startOfDay AND timestamp < :startOfNextDay")
    suspend fun getAllPastDay(startOfDay: Long, startOfNextDay: Long): List<StepsHourly>

    @Query("DELETE FROM steps_hourly_table WHERE timestamp == :startOfHour")
    suspend fun deleteEntryForHour(startOfHour: Long)

    @Query("DELETE FROM steps_hourly_table")
    suspend fun deleteAll()
}