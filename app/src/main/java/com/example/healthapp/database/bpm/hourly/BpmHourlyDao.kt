package com.example.healthapp.database.bpm.hourly

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BpmHourlyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bpmHourly: BpmHourly)

    @Query("SELECT * FROM bpm_hourly_table WHERE timestamp == :startOfHour")
    suspend fun getEntryForHour(startOfHour: Long): BpmHourly?

    @Query("SELECT * FROM bpm_hourly_table WHERE timestamp >= :startOfDay AND timestamp < :startOfNextDay")
    suspend fun getAllPastDay(startOfDay: Long, startOfNextDay: Long): List<BpmHourly>

    @Query("DELETE FROM bpm_hourly_table WHERE timestamp == :startOfHour")
    suspend fun deleteEntryForHour(startOfHour: Long)

    @Query("DELETE FROM bpm_hourly_table")
    suspend fun deleteAll()
}