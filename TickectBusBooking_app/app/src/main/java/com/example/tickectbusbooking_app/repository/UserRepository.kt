package com.example.tickectbusbooking_app.repository

import com.example.tickectbusbooking_app.database.UserDao
import com.example.tickectbusbooking_app.model.User

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun registerUser(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.authenticateUser(email, password)
    }

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

}