package com.example.com.pdm0126.ratematch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.com.pdm0126.ratematch.RateMatchApplication
import com.example.com.pdm0126.ratematch.screens.*
import androidx.compose.runtime.collectAsState
import com.example.com.pdm0126.ratematch.ui.viewmodel.AuthViewModel
import com.example.com.pdm0126.ratematch.ui.viewmodel.DashboardViewModel
import com.example.com.pdm0126.ratematch.ui.viewmodel.MatchDetailViewModel
import com.example.com.pdm0126.ratematch.ui.viewmodel.RankingViewModel
import kotlinx.serialization.Serializable

@Serializable data object Login
@Serializable data object Register
@Serializable data object Dashboard
@Serializable data object Settings
@Serializable data object Ranking // NUEVA RUTA
@Serializable data class MatchDetail(val matchId: Int)

@Composable
fun AppNavigation(
    isDarkMode: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appProvider = (context.applicationContext as RateMatchApplication).appProvider

    val authViewModel: AuthViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                AuthViewModel(appProvider.authRepository)
            }
        }
    )

    val currentUser = authViewModel.authState.collectAsState().value

    NavHost(
        navController = navController,
        startDestination = if (appProvider.authRepository.currentUser != null) Dashboard else Login
    ) {
        composable<Login> {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Dashboard) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Register) }
            )
        }
        composable<Register> {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Dashboard) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable<Dashboard> {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        DashboardViewModel(appProvider.matchRepository)
                    }
                }
            )
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToSettings = { navController.navigate(Settings) },
                onNavigateToMatchDetail = { id -> navController.navigate(MatchDetail(id)) },
                onNavigateToRanking = { navController.navigate(Ranking) } // NUEVA ACCIÓN
            )
        }
        composable<Settings> {
            SettingsScreen(
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkThemeChange,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Login) {
                        popUpTo(Dashboard) { inclusive = true }
                    }
                }
            )
        }
        composable<MatchDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<MatchDetail>()
            val matchDetailViewModel: MatchDetailViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        MatchDetailViewModel(route.matchId, appProvider.matchRepository)
                    }
                }
            )
            MatchDetailScreen(
                viewModel = matchDetailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Ranking> {
            val rankingViewModel: RankingViewModel = viewModel()
            RankingScreen(
                viewModel = rankingViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}