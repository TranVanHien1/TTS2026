package com.example.tts2026.di

import com.example.tts2026.data.auth.AuthRepository
import com.example.tts2026.data.auth.RoomAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        implementation: RoomAuthRepository
    ): AuthRepository
}
