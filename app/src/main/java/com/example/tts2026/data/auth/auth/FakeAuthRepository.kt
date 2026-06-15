package com.example.tts2026.data.auth.auth

import com.example.tts2026.data.auth.auth.AuthRepository
import com.example.tts2026.data.auth.Entity.UserEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FakeAuthRepository @Inject constructor() : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(800)
        return Result.success(Unit)
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        delay(800)
        return Result.success(Unit)
    }

    override fun listUsers(): Flow<List<UserEntity>> {
        return flowOf(emptyList())
    }
}