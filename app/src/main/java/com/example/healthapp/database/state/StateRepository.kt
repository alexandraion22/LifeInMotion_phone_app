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

    suspend fun updateIsWalking(id: Int, isWalking: Boolean){
        stateDao.updateIsWalking(id,isWalking)
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

    suspend fun updateTimestampStartedWorkout(id: Int, timestampStartedWorkout: Long){
        stateDao.updateTimestampStartedWorkout(id, timestampStartedWorkout)
    }

    suspend fun updateSleepCycle(id: Int, sleepCycle: Int){
        stateDao.updateSleepCycle(id,sleepCycle)
    }

    suspend fun updateSleepStage(id: Int, sleepStage: Int){
        stateDao.updateSleepStage(id, sleepStage)
    }

    suspend fun updateTimeLightSleep(id: Int, timeLightSleep: Int){
        stateDao.updateTimeLightSleep(id,timeLightSleep)
    }

    suspend fun updateTimeDeepSleep(id: Int, timeDeepSleep: Int){
        stateDao.updateTimeDeepSleep(id, timeDeepSleep)
    }

    suspend fun updateTimeREM(id: Int, timeREM: Int){
        stateDao.updateTimeREM(id,timeREM)

    }
}