package com.example.tts2026.ui.home

import com.example.tts2026.data.auth.Entity.CarEntity
import com.example.tts2026.data.auth.Entity.UserEntity

data class HomeUiState(
    val email: String = "",
    val selectedTab: Int = 0,
    val users: List<UserEntity> = emptyList(),
    val cars: List<CarEntity> = emptyList()
)
