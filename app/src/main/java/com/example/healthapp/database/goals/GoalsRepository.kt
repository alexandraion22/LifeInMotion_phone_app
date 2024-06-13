package com.example.healthapp.database.goals

import javax.inject.Inject

class GoalsRepository @Inject constructor(private val goalsDao: GoalsDao) {
    suspend fun insert(goals: Goals) {
        goalsDao.insert(goals)
    }

    suspend fun deleteAllGoals() {
        goalsDao.deleteAll()
    }

    suspend fun getFirst(): Goals {
        return goalsDao.getFirst()
    }
}