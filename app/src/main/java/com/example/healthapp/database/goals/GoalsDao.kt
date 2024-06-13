package com.example.healthapp.database.goals

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GoalsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goals: Goals)

    @Query("SELECT * FROM goals_table LIMIT 1")
    suspend fun getFirst(): Goals

    @Query("DELETE FROM goals_table")
    suspend fun deleteAll()
}