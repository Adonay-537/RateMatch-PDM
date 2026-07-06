package com.example.com.pdm0126.ratematch.data.repository

import com.example.com.pdm0126.ratematch.data.database.dao.UserDao
import com.example.com.pdm0126.ratematch.data.database.entities.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(name: String, email: String, passwordHash: String): Boolean {
        val existingUser = userDao.getUserByEmail(email)
        if (existingUser != null) {
            return false
        }

        val newUser = UserEntity(name = name, email = email, passwordHash = passwordHash)
        userDao.insertUser(newUser)
        return true
    }

    suspend fun loginUser(email: String, passwordHash: String): UserEntity? {
        val user = userDao.getUserByEmail(email)

        if (user != null && user.passwordHash == passwordHash) {
            return user
        }
        return null
    }
}