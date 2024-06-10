package com.example.healthapp.database.steps.hourly

import javax.inject.Inject

class StepsHourlyRepository @Inject constructor(private val stepsHourlyDao: StepsHourlyDao) {
    suspend fun insert(stepsHourly: StepsHourly) {
        stepsHourlyDao.insert(stepsHourly)
    }

    suspend fun getEntryForHour(startOfHour: Long): StepsHourly? {
        return stepsHourlyDao.getEntryForHour(startOfHour)
    }

    suspend fun update(updatedEntry: StepsHourly) {
        stepsHourlyDao.deleteEntryForHour(updatedEntry.timestamp)
        stepsHourlyDao.insert(updatedEntry)
    }

    suspend fun getAllPastDay(startOfDay: Long, startOfNextDay: Long): List<StepsHourly> {
        return stepsHourlyDao.getAllPastDay(startOfDay,startOfNextDay)
    }

    suspend fun deleteAll(){
        stepsHourlyDao.deleteAll()
    }
}