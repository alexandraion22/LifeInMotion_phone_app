package com.example.healthapp.database.state

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthapp.database.workouts.Workout

@Dao
interface StateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(state: State)

    @Query("SELECT * FROM state_table LIMIT 1")
    suspend fun getFirst(): State?

    @Query("DELETE FROM state_table")
    suspend fun deleteAll()

    @Query("UPDATE state_table SET isSleeping = :isSleeping WHERE id = :id")
    suspend fun updateIsSleeping(id: Int, isSleeping: Boolean)

    @Query("UPDATE state_table SET isWalking = :isWalking WHERE id = :id")
    suspend fun updateIsWalking(id: Int, isWalking: Boolean)

    @Query("UPDATE state_table SET isWorkingOutWatch = :isWorkingOutWatch WHERE id = :id")
    suspend fun updateIsWorkingOutWatch(id: Int, isWorkingOutWatch: Boolean)

    @Query("UPDATE state_table SET isWorkingOutBpm = :isWorkingOutBpm WHERE id = :id")
    suspend fun updateIsWorkingOutBpm(id: Int, isWorkingOutBpm: Boolean)

    @Query("UPDATE state_table SET stepsLast8Minutes = :stepsLast8Minutes WHERE id = :id")
    suspend fun updateStepsLast8Minutes(id: Int, stepsLast8Minutes: Int)

    @Query("UPDATE state_table SET stepsLast4Minutes = :stepsLast4Minutes WHERE id = :id")
    suspend fun updateStepsLast4Minutes(id: Int, stepsLast4Minutes: Int)

    @Query("UPDATE state_table SET caloriesConsumedBpm = :caloriesConsumedBpm WHERE id = :id")
    suspend fun updateCaloriesConsumedBpm(id: Int, caloriesConsumedBpm: Int)

    @Query("UPDATE state_table SET timestampLastSteps = :timestampLastSteps WHERE id = :id")
    suspend fun updateTimestampLastSteps(id: Int, timestampLastSteps: Long)

    @Query("UPDATE state_table SET timestampStartWorkout= :timestampStartedWorkout WHERE id = :id")
    suspend fun updateTimestampStartedWorkout(id: Int, timestampStartedWorkout: Long)

    @Query("UPDATE state_table SET sleepCycle = :sleepCycle WHERE id = :id")
    suspend fun updateSleepCycle(id: Int, sleepCycle: Int)

    @Query("UPDATE state_table SET sleepStage = :sleepStage WHERE id = :id")
    suspend fun updateSleepStage(id: Int, sleepStage: Int)

    @Query("UPDATE state_table SET timeLightSleep = :timeLightSleep WHERE id = :id")
    suspend fun updateTimeLightSleep(id: Int, timeLightSleep: Int)

    @Query("UPDATE state_table SET timeDeepSleep = :timeDeepSleep WHERE id = :id")
    suspend fun updateTimeDeepSleep(id: Int, timeDeepSleep: Int)

    @Query("UPDATE state_table SET timeREM = :timeREM WHERE id = :id")
    suspend fun updateTimeREM(id: Int, timeREM: Int)
}
