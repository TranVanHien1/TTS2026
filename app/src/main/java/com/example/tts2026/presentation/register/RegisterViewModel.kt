package com.example.tts2026.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tts2026.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Register flow: form input -> validate -> AuthRepository -> Room insert user.
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI collect StateFlow nay de hien loi field, loading va ket qua dang ky.
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = false) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = false) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(confirmPassword = confirmPassword, confirmPasswordError = false)
        }
    }

    fun register() {
        val state = _uiState.value
        // ViewModel giu logic validate de Composable khong bi phinh to.
        val emailError = state.email.isBlank()
        val passwordError = state.password.length < 6
        val confirmPasswordError =
            state.confirmPassword.isBlank() || state.confirmPassword != state.password

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                registerSucceeded = false,
                registerFailed = false
            )
        }

        if (emailError || passwordError || confirmPasswordError) {
            return
        }

        viewModelScope.launch {
            // Coroutine goi Room qua Repository, khong block UI thread.
            _uiState.update {
                it.copy(
                    isLoading = true,
                    registerSucceeded = false,
                    registerFailed = false
                )
            }

            authRepository.register(state.email, state.password)
                .onSuccess {
                    _uiState.update {
                        it.copy(isLoading = false, registerSucceeded = true)
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, registerFailed = true)
                    }
                }
        }
    }

    fun consumeRegisterResult() {
        // Reset event dang ky de snackbar/navigation khong lap lai.
        _uiState.update {
            it.copy(registerSucceeded = false, registerFailed = false)
        }
    }
}
