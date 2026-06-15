package com.example.tts2026.data.car

import com.example.tts2026.data.auth.Entity.CarEntity
import kotlinx.coroutines.flow.Flow

interface CarRepository {
    fun listCars(): Flow<List<CarEntity>>
    suspend fun addCar(name: String, model: String, year: Int, price: Double)
    suspend fun updateCar(car: CarEntity)
    suspend fun deleteCar(car: CarEntity)
}
