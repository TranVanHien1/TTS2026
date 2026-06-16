package com.example.tts2026.di

import com.example.tts2026.domain.repository.CarRepository
import com.example.tts2026.data.repository.RoomCarRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CarModule {

    // Bind interface voi implementation de HomeViewModel khong phu thuoc truc tiep vao Room.
    @Binds
    @Singleton
    abstract fun bindCarRepository(
        implementation: RoomCarRepository
    ): CarRepository
}
