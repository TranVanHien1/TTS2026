package com.example.tts2026.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val products: List<ProductDto>
)

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val rating: Double,
    val price: Double,
    val thumbnail: String
)
