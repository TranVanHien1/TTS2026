package com.example.tts2026.data.auth

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
