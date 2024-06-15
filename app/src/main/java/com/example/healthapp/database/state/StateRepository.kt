package com.example.healthapp.database.state

import javax.inject.Inject

class StateRepository @Inject constructor(private val stateDao: StateDao) {
    suspend fun insert(state: State) {
        stateDao.insert(state)
    }

    suspend fun deleteAllStates() {
        stateDao.deleteAll()
    }

    suspend fun getFirst(): State? {
        return stateDao.getFirst()
    }

    suspend fun updateIsSleeping(id: Int, isSleeping: Boolean){
        stateDao.updateIsSleeping(id,isSleeping)
    }

    suspend fun updateIsWorkingOutWatch(id: Int, isWorkingOutWatch: Boolean){
        stateDao.updateIsWorkingOutWatch(id,isWorkingOutWatch)
    }

    suspend fun updateIsWorkingOutBpm(id: Int, isWorkingOutBpm: Boolean){
        stateDao.updateIsWorkingOutBpm(id,isWorkingOutBpm)
    }

    suspend fun updateStepsLast8Minutes(id: Int, stepsLast8Minutes: Int){
        stateDao.updateStepsLast8Minutes(id,stepsLast8Minutes)
    }

    suspend fun updateStepsLast4Minutes(id: Int, stepsLast8Minutes: Int){
        stateDao.updateStepsLast4Minutes(id,stepsLast8Minutes)
    }

    suspend fun updateCaloriesConsumedBpm(id: Int, caloriesConsumedBpm: Int){
        stateDao.updateCaloriesConsumedBpm(id,caloriesConsumedBpm)
    }

    suspend fun updateTimestampLastSteps(id: Int, timestampLastSteps: Long){
        stateDao.updateTimestampLastSteps(id,timestampLastSteps)
    }
}