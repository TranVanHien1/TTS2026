package com.example.tts2026.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.tts2026.domain.repository.AuthRepository
import com.example.tts2026.data.local.dao.UserDao
import com.example.tts2026.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Repository an chi tiet Room. ViewModel chi biet AuthRepository, khong biet UserDao.
class RoomAuthRepository @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        // Room query chay trong suspend function, ket qua duoc doi thanh Result cho ViewModel.
        val user = userDao.findUser(email, password)
        return if (user != null) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Invalid email or password"))
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            // Email la primary key, nen can chan trung truoc khi insert.
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

    override fun listUsers(): Flow<List<UserEntity>> {
        // Room Flow tu dong emit lai khi bang users thay doi.
        return userDao.listUsers()
    }
}
