package com.example.tts2026.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tts2026.data.local.app_database.AppDatabase
import com.example.tts2026.data.local.dao.CarDao
import com.example.tts2026.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // Migration giu du lieu user cu khi them bang cars vao database version 2.
    private val migration1To2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS cars (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    model TEXT NOT NULL,
                    year INTEGER NOT NULL,
                    price REAL NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        // Room database la singleton de DAO/repository dung chung mot nguon du lieu.
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tts2026.db"
        )
            .addMigrations(migration1To2)
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        // Hilt lay DAO tu AppDatabase de inject vao RoomAuthRepository.
        return database.userDao()
    }

    @Provides
    fun provideCarDao(database: AppDatabase): CarDao {
        // Hilt lay DAO tu AppDatabase de inject vao RoomCarRepository.
        return database.carDao()
    }
}
