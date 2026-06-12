package com.example.tts2026.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tts2026.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = false) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = false) }
    }

    fun onPasswordVisibilityChange() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun login() {
        val currentState = _uiState.value
        val emailError = currentState.email.isBlank()
        val passwordError = currentState.password.length < MIN_PASSWORD_LENGTH

        if (emailError || passwordError) {
            _uiState.update {
                it.copy(emailError = emailError, passwordError = passwordError)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, loginSucceeded = false, loginFailed = false)
            }

            authRepository.login(currentState.email, currentState.password)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(isLoading = false, loginSucceeded = true)
                    }
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(isLoading = false, loginFailed = true)
                    }
                }
        }
    }

    fun consumeLoginResult() {
        _uiState.update { it.copy(loginSucceeded = false, loginFailed = false) }
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}
