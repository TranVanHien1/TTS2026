package com.example.tts2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tts2026.ui.home.HomeScreen
import com.example.tts2026.ui.login.LoginRoute
import com.example.tts2026.ui.register.RegisterRoute

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Login.route
    ) {
        composable(AppRoute.Login.route) {
            LoginRoute(
                onRegisterClick = {
                    navController.navigate(AppRoute.Register.route)
                },
                onLoginSuccess = {
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
            HomeScreen()
        }
    }
}
