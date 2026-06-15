package com.example.tts2026.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tts2026.data.auth.Entity.CarEntity
import com.example.tts2026.data.auth.auth.AuthRepository
import com.example.tts2026.data.car.CarRepository
import com.example.tts2026.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val carRepository: CarRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        authRepository.listUsers(),
        carRepository.listCars(),
        userPreferencesRepository.sessionEmail,
        userPreferencesRepository.selectedHomeTab
    ) { users, cars, email, selectedTab ->
        HomeUiState(
            email = email.orEmpty(),
            selectedTab = selectedTab,
            users = users,
            cars = cars
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    private val _logoutCompleted = MutableStateFlow(false)
    val logoutCompleted: StateFlow<Boolean> = _logoutCompleted.asStateFlow()

    fun selectTab(tab: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveSelectedHomeTab(tab)
        }
    }

    fun addCar(
        name: String,
        model: String,
        year: Int,
        price: Double
    ) {
        viewModelScope.launch {
            carRepository.addCar(
                name = name,
                model = model,
                year = year,
                price = price
            )
        }
    }

    fun updateCar(car: CarEntity) {
        viewModelScope.launch {
            carRepository.updateCar(car)
        }
    }

    fun deleteCar(car: CarEntity) {
        viewModelScope.launch {
            carRepository.deleteCar(car)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearSession()
            _logoutCompleted.value = true
        }
    }

    fun consumeLogout() {
        _logoutCompleted.value = false
    }
}
