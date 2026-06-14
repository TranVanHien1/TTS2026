package com.example.tts2026.ui.register

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val confirmPasswordError: Boolean = false,
    val isLoading: Boolean = false,
    val registerSucceeded: Boolean = false,
    val registerFailed: Boolean = false
)
