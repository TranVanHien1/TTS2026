package com.example.tts2026.di

import android.content.Context
import androidx.room.Room
import com.example.tts2026.data.local.AppDatabase
import com.example.tts2026.data.local.StickerImageDao
import com.example.tts2026.data.repository.DefaultStickerRepository
import com.example.tts2026.data.repository.StickerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        // Hilt tao 1 Room database singleton cho toan app.
        // StickerImageDao se duoc lay tu database nay.
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tts2026_sticker.db"
        ).build()
    }

    @Provides
    fun provideStickerImageDao(database: AppDatabase): StickerImageDao {
        // Repository can DAO de observe/insert sticker metadata.
        return database.stickerImageDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // OkHttpClient dung cho CloudinaryRemoteDataSource upload multipart file.
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        // Json parse response tu Cloudinary, bo qua field khong dung den.
        return Json {
            ignoreUnknownKeys = true
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStickerRepository(
        // Khi ViewModel yeu cau StickerRepository, Hilt se cap DefaultStickerRepository.
        implementation: DefaultStickerRepository
    ): StickerRepository
}
