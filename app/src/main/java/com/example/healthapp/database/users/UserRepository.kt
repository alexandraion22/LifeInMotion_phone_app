package com.example.healthapp.database.users

class UserRepository(private val userDao: UserDao) {
    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun getUser(): User? {
        return userDao.getUser()
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAll()
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }
}