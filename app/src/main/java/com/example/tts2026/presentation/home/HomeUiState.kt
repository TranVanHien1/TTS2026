package com.example.tts2026.presentation.home

import com.example.tts2026.data.local.entity.CarEntity
import com.example.tts2026.data.local.entity.UserEntity

data class HomeUiState(
    val email: String = "",
    val selectedTab: Int = 0,
    val users: List<UserEntity> = emptyList(),
    val cars: List<CarEntity> = emptyList()
)
