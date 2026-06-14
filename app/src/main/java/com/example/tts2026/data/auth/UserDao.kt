package com.example.tts2026.data.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun findUser(email: String, password: String): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    suspend fun emailExists(email: String): Boolean
}
