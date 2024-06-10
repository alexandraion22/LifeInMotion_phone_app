package com.example.healthapp.database.steps.daily

import javax.inject.Inject

class StepsDailyRepository @Inject constructor(private val stepsDailyDao: StepsDailyDao) {
    suspend fun insert(stepsDaily: StepsDaily) {
        stepsDailyDao.insert(stepsDaily)
    }

    suspend fun deleteAll() {
        stepsDailyDao.deleteAll()
    }


    suspend fun getEntryForDay(startOfDay: Long): StepsDaily? {
        return stepsDailyDao.getEntryForDay(startOfDay)
    }

    suspend fun getAllPast7days(startOfWeek: Long, startOfNextWeek: Long): List<StepsDaily> {
        return stepsDailyDao.getAllPast7days(startOfWeek,startOfNextWeek)
    }

    suspend fun getAllPast31days(startOfMonth: Long, startOfNextMonth: Long): List<StepsDaily> {
        return stepsDailyDao.getAllPast31days(startOfMonth, startOfNextMonth)
    }

    suspend fun deleteEntryForDay(startOfDay: Long) {
        stepsDailyDao.deleteEntryForDay(startOfDay)
    }

    suspend fun update(updatedEntry: StepsDaily) {
        stepsDailyDao.deleteEntryForDay(updatedEntry.timestamp)
        stepsDailyDao.insert(updatedEntry)
    }
}