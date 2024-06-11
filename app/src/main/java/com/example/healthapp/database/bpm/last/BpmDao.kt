package com.example.healthapp.database.bpm.last

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BpmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bpm: Bpm)

    @Query("SELECT * FROM bpm_table LIMIT 1")
    suspend fun getFirst(): Bpm

    @Query("DELETE FROM bpm_table")
    suspend fun deleteAll()
}