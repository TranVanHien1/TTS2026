package com.example.tts2026.di

import com.example.tts2026.data.product.ProductRepository
import com.example.tts2026.data.product.RemoteProductRepository
import com.example.tts2026.data.remote.ProductApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // @Provides dung cho object tao qua Builder hoac thu vien ben ngoai.
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
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
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        // Hilt lay OkHttpClient o provider tren de tao mot Retrofit singleton.
        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi {
        // Retrofit tao implementation cua interface ProductApi.
        return retrofit.create(ProductApi::class.java)
    }
}

// Noi abstraction ProductRepository voi implementation dung Retrofit.
@Module
@InstallIn(SingletonComponent::class)
abstract class ProductRepositoryModule {

    // ViewModel yeu cau ProductRepository se nhan RemoteProductRepository.
    @Binds
    @Singleton
    abstract fun bindProductRepository(
        implementation: RemoteProductRepository
    ): ProductRepository
}
