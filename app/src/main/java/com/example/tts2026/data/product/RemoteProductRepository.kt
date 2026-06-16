package com.example.tts2026.data.product

import com.example.tts2026.data.remote.ProductApi
import com.example.tts2026.data.remote.ProductDto
import javax.inject.Inject

// Hilt dung @Inject constructor de tao Repository va cap ProductApi.
class RemoteProductRepository @Inject constructor(
    private val productApi: ProductApi
) : ProductRepository {

    override suspend fun getProducts(): Result<List<ProductDto>> {
        // Repository goi data source va chuyen loi/thanh cong thanh Result.
        return runCatching {
            productApi.getProducts().products
        }
    }
}
