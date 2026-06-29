package com.example.com.pdm0126.ratematch.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
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
    val backStack = rememberNavBackStack<NavKey>(Routes.Login)
    val dispatcherOwner = rememberNavigationEventDispatcherOwner()

    CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides dispatcherOwner) {
        NavDisplay(
            backStack = backStack,
            onBack = { popCount ->
                repeat(popCount) {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.size - 1)
                    }
                }
            },
            entryProvider = entryProvider {
                entry<Routes.Login> {
                    LoginScreen(
                        onLoginSuccess = {
                            backStack.add(Routes.Dashboard)
                        }
                    )
                }

                entry<Routes.Dashboard> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "¡Bienvenido al Dashboard de RateMatch!")
                    }
                }
            }
        )
    }
}