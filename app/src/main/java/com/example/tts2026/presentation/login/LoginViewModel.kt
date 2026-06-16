package com.example.tts2026.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tts2026.domain.repository.AuthRepository
import com.example.tts2026.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Login flow: UI event -> validate -> AuthRepository(Room) -> DataStore session -> UI state.
@HiltViewModel
class LoginViewModel @Inject constructor(
    // Hilt inject repository that knows how to login against Room.
    private val authRepository: AuthRepository,
    // Luu email dang nhap de lan sau mo app co the vao Home ngay.
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // MutableStateFlow nam trong ViewModel; UI chi collect ban read-only ben duoi.
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
        // Validate o ViewModel de UI chi can hien thi state loi.
        val emailError = currentState.email.isBlank()
        val passwordError = currentState.password.length < MIN_PASSWORD_LENGTH

        if (emailError || passwordError) {
            _uiState.update {
                it.copy(emailError = emailError, passwordError = passwordError)
            }
            return
        }

        viewModelScope.launch {
            // Emit loading state, Compose se recompose va hien CircularProgressIndicator.
            _uiState.update {
                it.copy(isLoading = true, loginSucceeded = false, loginFailed = false)
            }

            authRepository.login(currentState.email, currentState.password)
                .onSuccess {
                    // Login thanh cong moi luu session vao DataStore.
                    userPreferencesRepository.saveSession(currentState.email)
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
        // Reset one-time event de LaunchedEffect khong navigate lai khi recomposition.
        _uiState.update { it.copy(loginSucceeded = false, loginFailed = false) }
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}
