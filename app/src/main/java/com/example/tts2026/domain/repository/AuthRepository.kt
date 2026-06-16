package com.example.tts2026.domain.repository

import com.example.tts2026.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String): Result<Unit>
    fun listUsers(): Flow<List<UserEntity>>
}
