package com.example.tts2026.data.product

import com.example.tts2026.data.remote.ProductDto

// Contract giup ViewModel khong phu thuoc truc tiep vao Retrofit.
interface ProductRepository {
    // Repository goi data source va chuyen loi/thanh cong thanh Result
    // Suspend giu luong bat dong bo
    suspend fun getProducts(): Result<List<ProductDto>>
}
