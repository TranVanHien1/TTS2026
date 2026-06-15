package com.example.tts2026.data.car

import com.example.tts2026.data.auth.DAO.CarDao
import com.example.tts2026.data.auth.Entity.CarEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomCarRepository @Inject constructor(
    private val carDao: CarDao
) : CarRepository {

    override fun listCars(): Flow<List<CarEntity>> {
        return carDao.listCars()
    }

    override suspend fun addCar(
        name: String,
        model: String,
        year: Int,
        price: Double
    ) {
        carDao.insertCar(
            CarEntity(
                name = name,
                model = model,
                year = year,
                price = price
            )
        )
    }

    override suspend fun updateCar(car: CarEntity) {
        carDao.updateCar(car)
    }

    override suspend fun deleteCar(car: CarEntity) {
        carDao.deleteCar(car)
    }
}
