package com.example.healthapp.database.bpm.daily

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BpmDailyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bpmDaily: BpmDaily)

    @Query("SELECT * FROM bpm_daily_table WHERE timestamp == :startOfDay")
    suspend fun getEntryForDay(startOfDay: Long): BpmDaily?

    @Query("SELECT * FROM bpm_daily_table WHERE timestamp >= :startOfWeek AND timestamp < :startOfNextWeek")
    suspend fun getAllPast7days(startOfWeek: Long, startOfNextWeek: Long): List<BpmDaily>

    @Query("SELECT * FROM bpm_daily_table WHERE timestamp >= :startOfMonth AND timestamp < :startOfNextMonth")
    suspend fun getAllPast31days(startOfMonth: Long, startOfNextMonth: Long): List<BpmDaily>

    @Query("DELETE FROM bpm_daily_table WHERE timestamp == :startOfDay")
    suspend fun deleteEntryForDay(startOfDay: Long)

    @Query("DELETE FROM bpm_daily_table")
    suspend fun deleteAll()
}