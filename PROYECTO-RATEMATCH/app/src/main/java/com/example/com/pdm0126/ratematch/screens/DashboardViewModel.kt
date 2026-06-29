package com.example.com.pdm0126.ratematch.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.com.pdm0126.ratematch.RateMatchApplication
import com.example.com.pdm0126.ratematch.data.model.LeagueWithMatches
import com.example.com.pdm0126.ratematch.data.repository.LeagueRepository
import com.example.com.pdm0126.ratematch.data.repository.MatchRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val leagueRepository: LeagueRepository,
    private val matchRepository: MatchRepository
) : ViewModel() {

    // Escucha activamente la base de datos local (Fuente de la verdad)
    val leaguesWithMatches: StateFlow<List<LeagueWithMatches>> =
        leagueRepository.getLeaguesWithMatches()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        // En cuanto el ViewModel se crea, mandamos a llamar a internet de forma asíncrona
        syncMatchesFromApi()
    }

    private fun syncMatchesFromApi() {
        viewModelScope.launch {
            // El repositorio descarga mediante Ktor y guarda automáticamente en Room
            matchRepository.fetchAndSyncMatches()
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as RateMatchApplication
                DashboardViewModel(
                    leagueRepository = app.appProvider.leagueRepository,
                    matchRepository = app.appProvider.matchRepository
                )
            }
        }
    }
}