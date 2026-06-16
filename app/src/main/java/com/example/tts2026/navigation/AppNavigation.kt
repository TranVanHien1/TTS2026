package com.example.tts2026.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tts2026.presentation.home.HomeRoute
import com.example.tts2026.presentation.login.LoginRoute
import com.example.tts2026.presentation.register.RegisterRoute

@Composable
fun AppNavigation(
    viewModel: AppNavigationViewModel = hiltViewModel()
) {
    // Collect session tu DataStore. Khi sessionEmail thay doi, AppNavigation co state moi.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val navController = rememberNavController()
    // Co session thi vao Home, chua co session thi vao Login.
    val startDestination = if (uiState.sessionEmail.isNullOrBlank()) {
        AppRoute.Login.route
    } else {
        AppRoute.Home.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppRoute.Login.route) {
            LoginRoute(
                onRegisterClick = {
                    navController.navigate(AppRoute.Register.route)
                },
                onLoginSuccess = {
                    // LoginViewModel da luu email vao DataStore, Navigation chi doi man hinh.
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AppRoute.Register.route) {
            RegisterRoute(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoute.Home.route) {
            HomeRoute(
                onLogout = {
                    // HomeViewModel xoa session trong DataStore, Navigation dua user ve Login.
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
