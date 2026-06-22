package com.example.com.pdm0126.ratematch.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.com.pdm0126.ratematch.screens.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class Routes : NavKey {
    @Serializable
    data object Login : Routes()
    @Serializable
    data object Dashboard : Routes()
}

@Composable
fun AppNavigation() {
    // Navigation 3 requiere que las rutas implementen NavKey y sean @Serializable.
    // El backStack se maneja como una MutableList<NavKey>.
    val backStack = rememberNavBackStack(Routes.Login)

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.size - 1)
            }
        },
        entryProvider = entryProvider<NavKey> {
            // Pantalla 1: Login
            entry<Routes.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        // Navegamos al Dashboard al tener éxito
                        backStack.add(Routes.Dashboard)
                    }
                )
            }

            // Pantalla 2: Dashboard (Placeholder temporal)
            entry<Routes.Dashboard> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "¡Bienvenido al Dashboard de RateMatch!")
                }
            }
        }
    )
}
