package com.example.healthapp.database.calories

import javax.inject.Inject

class CaloriesDailyRepository @Inject constructor(private val bpmDailyDao: CaloriesDailyDao) {
    suspend fun insert(bpmDaily: CaloriesDaily) {
        bpmDailyDao.insert(bpmDaily)
    }

    suspend fun deleteAll() {
        bpmDailyDao.deleteAll()
    }


    suspend fun getEntryForDay(startOfDay: Long): CaloriesDaily? {
        return bpmDailyDao.getEntryForDay(startOfDay)
    }

    suspend fun getAllPast7days(startOfWeek: Long, startOfNextWeek: Long): List<CaloriesDaily> {
        return bpmDailyDao.getAllPast7days(startOfWeek,startOfNextWeek)
    }

    suspend fun getAllPast31days(startOfMonth: Long, startOfNextMonth: Long): List<CaloriesDaily> {
        return bpmDailyDao.getAllPast31days(startOfMonth, startOfNextMonth)
    }

    suspend fun deleteEntryForDay(startOfDay: Long) {
        bpmDailyDao.deleteEntryForDay(startOfDay)
    }

    suspend fun update(updatedEntry: CaloriesDaily) {
        bpmDailyDao.deleteEntryForDay(updatedEntry.timestamp)
        bpmDailyDao.insert(updatedEntry)
    }
}