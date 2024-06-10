package com.example.healthapp.database.bpm.daily

import androidx.room.Query
import com.example.healthapp.database.bpm.hourly.BpmHourly
import javax.inject.Inject

class BpmDailyRepository @Inject constructor(private val bpmDailyDao: BpmDailyDao) {
    suspend fun insert(bpmDaily: BpmDaily) {
        bpmDailyDao.insert(bpmDaily)
    }

    suspend fun deleteAll() {
        bpmDailyDao.deleteAll()
    }


    suspend fun getEntryForDay(startOfDay: Long): BpmDaily? {
        return bpmDailyDao.getEntryForDay(startOfDay)
    }

    suspend fun getAllPast7days(startOfWeek: Long, startOfNextWeek: Long): List<BpmDaily> {
        return bpmDailyDao.getAllPast7days(startOfWeek,startOfNextWeek)
    }

    suspend fun getAllPast31days(startOfMonth: Long, startOfNextMonth: Long): List<BpmDaily> {
        return bpmDailyDao.getAllPast31days(startOfMonth, startOfNextMonth)
    }

    suspend fun deleteEntryForDay(startOfDay: Long) {
        bpmDailyDao.deleteEntryForDay(startOfDay)
    }

    suspend fun update(updatedEntry: BpmDaily) {
        bpmDailyDao.deleteEntryForDay(updatedEntry.timestamp)
        bpmDailyDao.insert(updatedEntry)
    }
}