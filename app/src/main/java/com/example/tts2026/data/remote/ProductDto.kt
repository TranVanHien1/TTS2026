package com.example.tts2026.data.remote

data class ProductResponse(
    val products: List<ProductDto>
)

data class ProductDto(
    val id: Int,
    val title: String,
    val rating: Double,
    val price: Double,
    val thumbnail: String
)
