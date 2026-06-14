package com.example.tts2026.data.auth

import kotlinx.coroutines.delay
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
}
