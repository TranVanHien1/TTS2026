package com.example.tts2026.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// Data source: Retrofit tao implementation de goi HTTP API.
// Suspend: cho phep cho phan hoi mang tra ve ket qua.
interface ProductApi {

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 30
    ): ProductResponse
}
