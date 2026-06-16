package com.example.tts2026.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tts2026.data.local.entity.CarEntity
import com.example.tts2026.domain.repository.AuthRepository
import com.example.tts2026.domain.repository.CarRepository
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

// Home flow: Room users + Room cars + DataStore session/tab -> HomeUiState -> HomeScreen.
@HiltViewModel
class HomeViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val carRepository: CarRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // combine gom 4 Flow doc lap thanh mot UI state duy nhat cho man Home.
    val uiState: StateFlow<HomeUiState> = combine(
        authRepository.listUsers(),
        carRepository.listCars(),
        userPreferencesRepository.sessionEmail,
        userPreferencesRepository.selectedHomeTab
    ) { users, cars, email, selectedTab ->
        // Moi lan Room/DataStore emit, HomeUiState moi duoc tao va UI recompose.
        HomeUiState(
            email = email.orEmpty(),
            selectedTab = selectedTab,
            users = users,
            cars = cars
        )
    }
        .stateIn(
            scope = viewModelScope,
            // Khi UI khong collect nua, upstream Flow duoc giu 5 giay roi dung.
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    // Event mot lan cho logout. Dung rieng voi HomeUiState de tranh navigate lap lai.
    private val _logoutCompleted = MutableStateFlow(false)
    val logoutCompleted: StateFlow<Boolean> = _logoutCompleted.asStateFlow()

    fun selectTab(tab: Int) {
        viewModelScope.launch {
            // Luu tab vao DataStore, HomeUiState se tu cap nhat qua selectedHomeTab Flow.
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
            // Them car vao Room; listCars Flow emit lai danh sach moi.
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
            // Sua car trong Room; Home khong can tu reload vi Flow se emit.
            carRepository.updateCar(car)
        }
    }

    fun deleteCar(car: CarEntity) {
        viewModelScope.launch {
            // Xoa car trong Room; Flow danh sach xe cap nhat tu dong.
            carRepository.deleteCar(car)
        }
    }

    fun logout() {
        viewModelScope.launch {
            // Xoa session trong DataStore roi phat event de Navigation quay ve Login.
            userPreferencesRepository.clearSession()
            _logoutCompleted.value = true
        }
    }

    fun consumeLogout() {
        // Reset event logout sau khi Navigation da xu ly.
        _logoutCompleted.value = false
    }
}
