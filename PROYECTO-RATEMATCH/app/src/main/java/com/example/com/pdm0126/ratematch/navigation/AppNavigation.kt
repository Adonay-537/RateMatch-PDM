package com.example.com.pdm0126.ratematch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.example.com.pdm0126.ratematch.screens.LoginScreen
import com.example.com.pdm0126.ratematch.screens.DashboardScreen
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
    val backStack = rememberNavBackStack(Routes.Login)
    val dispatcherOwner = rememberNavigationEventDispatcherOwner()

    CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides dispatcherOwner) {
        NavDisplay(
            backStack = backStack,
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeAt(backStack.size - 1)
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

                    DashboardScreen()
                }
            }
        )
    }
}