package com.example.tts2026.ui.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val isLoading: Boolean = false,
    val loginSucceeded: Boolean = false,
    val loginFailed: Boolean = false
)
