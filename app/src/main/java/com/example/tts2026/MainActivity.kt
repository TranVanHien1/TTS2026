package com.example.tts2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tts2026.ui.sticker.StickerUploadRoute
import com.example.tts2026.ui.theme.TTS2026Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TTS2026Theme {
                // Entry point cua app:
                // Activity chi gan theme va mo man hinh upload sticker.
                // Cac dependency nhu ViewModel/Repository/Room/OkHttp duoc Hilt cap o cac tang ben duoi.
                StickerUploadRoute()
            }
        }
    }
}
