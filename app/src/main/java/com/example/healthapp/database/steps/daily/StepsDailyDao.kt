package com.example.healthapp.database.steps.daily

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StepsDailyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stepsDaily: StepsDaily)

    @Query("SELECT * FROM steps_daily_table WHERE timestamp == :startOfDay")
    suspend fun getEntryForDay(startOfDay: Long): StepsDaily?

    @Query("SELECT * FROM steps_daily_table WHERE timestamp >= :startOfWeek AND timestamp < :startOfNextWeek")
    suspend fun getAllPast7days(startOfWeek: Long, startOfNextWeek: Long): List<StepsDaily>

    @Query("SELECT * FROM steps_daily_table WHERE timestamp >= :startOfMonth AND timestamp < :startOfNextMonth")
    suspend fun getAllPast31days(startOfMonth: Long, startOfNextMonth: Long): List<StepsDaily>

    @Query("DELETE FROM steps_daily_table WHERE timestamp == :startOfDay")
    suspend fun deleteEntryForDay(startOfDay: Long)

    @Query("DELETE FROM steps_daily_table")
    suspend fun deleteAll()
}