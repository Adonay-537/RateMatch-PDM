package com.example.com.pdm0126.ratematch

import android.app.Application
import com.example.com.pdm0126.ratematch.data.AppProvider

class RateMatchApplication : Application() {
    // Inicializamos el AppProvider de forma perezosa (lazy)
    val appProvider by lazy { AppProvider(this) }
}