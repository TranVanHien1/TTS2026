package com.example.tts2026.data.auth.auth

import com.example.tts2026.data.auth.Entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String): Result<Unit>
    fun listUsers(): Flow<List<UserEntity>>
}