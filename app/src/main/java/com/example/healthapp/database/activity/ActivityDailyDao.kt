package com.example.healthapp.database.activity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ActivityDailyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(caloriesDaily: ActivityDaily)

    @Query("SELECT * FROM activity_daily_table WHERE timestamp == :startOfDay")
    suspend fun getEntryForDay(startOfDay: Long): ActivityDaily?

    @Query("SELECT * FROM activity_daily_table WHERE timestamp >= :startOfWeek AND timestamp < :startOfNextWeek")
    suspend fun getAllPast7days(startOfWeek: Long, startOfNextWeek: Long): List<ActivityDaily>

    @Query("SELECT * FROM activity_daily_table WHERE timestamp >= :startOfMonth AND timestamp < :startOfNextMonth")
    suspend fun getAllPast31days(startOfMonth: Long, startOfNextMonth: Long): List<ActivityDaily>

    @Query("DELETE FROM activity_daily_table WHERE timestamp == :startOfDay")
    suspend fun deleteEntryForDay(startOfDay: Long)

    @Query("DELETE FROM activity_daily_table")
    suspend fun deleteAll()
}