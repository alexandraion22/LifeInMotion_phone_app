package com.example.healthapp.database.users

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user_table ORDER BY id DESC LIMIT 1")
    suspend fun getUser(): User

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()
}