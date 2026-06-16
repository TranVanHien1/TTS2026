package com.example.tts2026.data.local.app_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tts2026.data.local.dao.CarDao
import com.example.tts2026.data.local.dao.UserDao
import com.example.tts2026.data.local.entity.CarEntity
import com.example.tts2026.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, CarEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun carDao(): CarDao
}
