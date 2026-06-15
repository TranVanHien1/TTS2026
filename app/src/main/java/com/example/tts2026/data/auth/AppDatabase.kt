package com.example.tts2026.data.auth

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tts2026.data.auth.DAO.CarDao
import com.example.tts2026.data.auth.DAO.UserDao
import com.example.tts2026.data.auth.Entity.CarEntity
import com.example.tts2026.data.auth.Entity.UserEntity

@Database(
    entities = [UserEntity::class, CarEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun carDao(): CarDao
}
