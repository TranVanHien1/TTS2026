package com.example.tts2026.di

import com.example.tts2026.data.car.CarRepository
import com.example.tts2026.data.car.RoomCarRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CarModule {

    @Binds
    @Singleton
    abstract fun bindCarRepository(
        implementation: RoomCarRepository
    ): CarRepository
}
