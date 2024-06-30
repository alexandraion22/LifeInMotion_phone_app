package com.example.healthapp.database.sleep

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SleepDailyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sleepDaily: SleepDaily)

    @Query("SELECT * FROM sleep_table WHERE timestampStart >= :previousDay8Pm AND timestampStart <= :today8Pm")
    suspend fun getEntriesForDay(previousDay8Pm: Long, today8Pm: Long): List<SleepDaily>

    @Query("SELECT * FROM sleep_table WHERE timestampStart >= :startOfWeek")
    suspend fun getAllPast7days(startOfWeek: Long): List<SleepDaily>

    @Query("DELETE FROM sleep_table WHERE id == :idGiven")
    suspend fun deleteEntryForId(idGiven: Int)

    @Query("UPDATE sleep_table SET automaticScore = :automaticScore WHERE id = :id")
    suspend fun updateAutomaticScore(id: Int, automaticScore: Int)

    @Query("UPDATE sleep_table SET givenScore = :givenScore WHERE id = :id")
    suspend fun updateManualScore(id: Int, givenScore: Int)

    @Query("DELETE FROM sleep_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM sleep_table")
    suspend fun getAll(): List<SleepDaily>
}