package com.example.healthapp.database.calories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CaloriesDailyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(caloriesDaily: CaloriesDaily)

    @Query("SELECT * FROM calories_daily_table WHERE timestamp == :startOfDay")
    suspend fun getEntryForDay(startOfDay: Long): CaloriesDaily?

    @Query("SELECT * FROM calories_daily_table WHERE timestamp >= :startOfWeek AND timestamp < :startOfNextWeek")
    suspend fun getAllPast7days(startOfWeek: Long, startOfNextWeek: Long): List<CaloriesDaily>

    @Query("SELECT * FROM calories_daily_table WHERE timestamp >= :startOfMonth AND timestamp < :startOfNextMonth")
    suspend fun getAllPast31days(startOfMonth: Long, startOfNextMonth: Long): List<CaloriesDaily>

    @Query("DELETE FROM calories_daily_table WHERE timestamp == :startOfDay")
    suspend fun deleteEntryForDay(startOfDay: Long)

    @Query("DELETE FROM calories_daily_table")
    suspend fun deleteAll()
}