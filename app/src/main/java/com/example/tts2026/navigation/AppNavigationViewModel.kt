package com.example.tts2026.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tts2026.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Doc DataStore de quyet dinh app mo vao Login hay Home.
@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // DataStore Flow -> StateFlow de Compose collect theo lifecycle.
    val uiState: StateFlow<AppNavigationUiState> =
        userPreferencesRepository.sessionEmail
            .map { email ->
                AppNavigationUiState(
                    isLoading = false,
                    sessionEmail = email
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppNavigationUiState()
            )
}
