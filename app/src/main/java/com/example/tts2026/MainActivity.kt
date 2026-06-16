package com.example.tts2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tts2026.navigation.AppNavigation
import com.example.tts2026.presentation.theme.TTS2026Theme
import dagger.hilt.android.AndroidEntryPoint

// Entry point cua Hilt o tang Activity, cho phep cac Composable dung hiltViewModel().
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TTS2026Theme {
                // Single Activity: toan bo dieu huong Login/Register/Home nam trong AppNavigation.
                AppNavigation()
            }
        }
    }
}
