package com.example.healthapp.database.bpm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BpmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bpm: Bpm)

    @Query("SELECT * FROM bpm_table WHERE timestamp >= datetime('now', '-1 hour')")
    suspend fun getAllPastHour(): List<Bpm>

    @Query("DELETE FROM bpm_table")
    suspend fun deleteAll()
}