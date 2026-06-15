package com.example.tts2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tts2026.ui.movie.MovieRoute
import com.example.tts2026.ui.theme.TTS2026Theme
import dagger.hilt.android.AndroidEntryPoint

// Cho phep Activity va cac Composable ben trong su dung dependency do Hilt quan ly.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TTS2026Theme {
                // View chi mo man hinh, khong tu tao Retrofit hay Repository.
                MovieRoute()
            }
        }
    }
}
