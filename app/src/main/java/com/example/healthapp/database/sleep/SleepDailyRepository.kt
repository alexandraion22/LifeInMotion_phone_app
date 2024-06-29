package com.example.healthapp.database.sleep

import androidx.room.Query
import javax.inject.Inject

class SleepDailyRepository @Inject constructor(private val sleepDailyDao: SleepDailyDao) {
    suspend fun insert(sleepDaily: SleepDaily) {
        sleepDailyDao.insert(sleepDaily)
    }

    suspend fun deleteAll() {
        sleepDailyDao.deleteAll()
    }

    suspend fun getEntriesForDay(previousDay8Pm: Long): List<SleepDaily> {
        return sleepDailyDao.getEntriesForDay(previousDay8Pm = previousDay8Pm)
    }

    suspend fun getAllPast7days(startOfWeek8Pm: Long): List<SleepDaily> {
        return sleepDailyDao.getAllPast7days(startOfWeek8Pm)
    }

    suspend fun updateAutomaticScore(id: Int, automaticScore: Int) {
        sleepDailyDao.updateAutomaticScore(id = id, automaticScore = automaticScore)
    }

    suspend fun updateManualScore(id: Int, givenScore: Int) {
        sleepDailyDao.updateManualScore(id = id, givenScore = givenScore)
    }

    suspend fun deleteEntryForId(idGiven: Int){
        sleepDailyDao.deleteEntryForId(idGiven)
    }
}