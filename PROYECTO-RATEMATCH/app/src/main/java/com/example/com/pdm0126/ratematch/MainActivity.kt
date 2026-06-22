package com.example.com.pdm0126.ratematch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.com.pdm0126.ratematch.navigation.AppNavigation
import com.example.com.pdm0126.ratematch.ui.theme.RATEMATCHTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RATEMATCHTheme() { //Esto es lo único que cambiara
                AppNavigation()
            }
        }
    }
}