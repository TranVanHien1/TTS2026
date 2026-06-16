package com.example.tts2026

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Root cua Hilt: tao dependency graph cho toan app truoc khi MainActivity chay.
@HiltAndroidApp
class TTSApplication : Application()
