package com.example.com.pdm0126.ratematch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.com.pdm0126.ratematch.navigation.AppNavigation
import com.example.com.pdm0126.ratematch.ui.theme.RATEMATCHTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkMode by rememberSaveable { mutableStateOf(systemInDarkTheme) }

            RATEMATCHTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    isDarkMode = isDarkMode,
                    onDarkThemeChange = { isDarkMode = it }
                )
            }
        }
    }
}