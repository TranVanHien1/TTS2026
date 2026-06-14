package com.example.tts2026.data.auth

import android.database.sqlite.SQLiteConstraintException
import javax.inject.Inject

class RoomAuthRepository @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        val user = userDao.findUser(email, password)
        return if (user != null) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Invalid email or password"))
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            if (userDao.emailExists(email)) {
                Result.failure(IllegalArgumentException("Email already exists"))
            } else {
                userDao.insertUser(UserEntity(email = email, password = password))
                Result.success(Unit)
            }
        } catch (exception: SQLiteConstraintException) {
            Result.failure(exception)
        }
    }
}
