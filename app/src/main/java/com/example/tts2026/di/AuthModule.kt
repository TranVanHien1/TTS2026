package com.example.tts2026.di

import com.example.tts2026.domain.repository.AuthRepository
import com.example.tts2026.data.repository.RoomAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    // Khi class can AuthRepository, Hilt se dua RoomAuthRepository vao.
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        implementation: RoomAuthRepository
    ): AuthRepository
}
