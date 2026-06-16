package com.example.tts2026.data.repository

import com.example.tts2026.data.local.dao.CarDao
import com.example.tts2026.data.local.entity.CarEntity
import com.example.tts2026.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Repository cho Car CRUD: HomeViewModel goi interface, repository moi dung CarDao.
class RoomCarRepository @Inject constructor(
    private val carDao: CarDao
) : CarRepository {

    override fun listCars(): Flow<List<CarEntity>> {
        // Flow nay cap nhat HomeUiState moi khi them/sua/xoa car trong Room.
        return carDao.listCars()
    }

    override suspend fun addCar(
        name: String,
        model: String,
        year: Int,
        price: Double
    ) {
        // Tao entity tu form input roi insert vao bang cars.
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
        // Update dua tren primary key id cua CarEntity.
        carDao.updateCar(car)
    }

    override suspend fun deleteCar(car: CarEntity) {
        // Delete entity hien tai, Room dung id de xoa dong tuong ung.
        carDao.deleteCar(car)
    }
}
