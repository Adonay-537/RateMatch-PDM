package com.example.com.pdm0126.ratematch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.com.pdm0126.ratematch.data.model.Match
import com.example.com.pdm0126.ratematch.data.remote.KtorClient
import com.example.com.pdm0126.ratematch.data.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface DashboardState {
    object Loading : DashboardState
    data class Success(val matches: List<Match>) : DashboardState
    data class Error(val message: String) : DashboardState
}

class DashboardViewModel(private val repository: MatchRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    private val _isLiveOnly = MutableStateFlow(false)
    val isLiveOnly: StateFlow<Boolean> = _isLiveOnly.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val leaguePriority = mapOf(
        1 to 1,      // World Cup
        4 to 2,      // Euro Championship
        9 to 3,      // Copa America
        13 to 4,     // Libertadores
        140 to 5,    // La Liga
        39 to 6,     // Premier League
        78 to 7,     // Bundesliga
        135 to 8,    // Serie A
        61 to 9,     // Ligue 1
        71 to 10,    // Serie A (Brasil)
        339 to 11    // Primera Division (El Salvador)
    )

    init {
        viewModelScope.launch {
            KtorClient.apiKeyFlow.collectLatest { key ->
                if (key.isNotEmpty() && key != "8937397c72444c139c80d19f85c7c25c") {
                    loadMatches()
                }
            }
        }
        loadMatches()
    }

    fun loadMatches() {
        viewModelScope.launch {
            _uiState.value = DashboardState.Loading
            try {
                val date = _selectedDate.value
                val dateStr = date.toString()
                
                android.util.Log.d("DashboardViewModel", "Cargando partidos para fecha local: $dateStr")
                
                // Ahora confiamos en que la API nos de los partidos correctos para nuestro día y zona horaria
                val matches = repository.getMatchesForDate(dateStr)
                
                // Ordenamos por prioridad de liga
                val sortedMatches = matches.sortedWith(compareBy(
                    { leaguePriority[it.leagueId] ?: 999 },
                    { it.leagueName },
                    { it.utcDate }
                ))
                
                _uiState.value = DashboardState.Success(sortedMatches)
            } catch (e: Exception) {
                if (e.message?.contains("token", true) == true && KtorClient.getApiKey().length < 10) {
                     _uiState.value = DashboardState.Loading 
                } else {
                    _uiState.value = DashboardState.Error(e.message ?: "Error de conexión")
                }
            }
        }
    }

    fun refreshMatches() {
        loadMatches()
    }

    fun toggleLiveFilter() {
        _isLiveOnly.value = !_isLiveOnly.value
    }

    fun changeDate(days: Int) {
        _selectedDate.value = _selectedDate.value.plusDays(days.toLong())
        loadMatches()
    }

    fun setDate(date: LocalDate) {
        _selectedDate.value = date
        loadMatches()
    }

    fun toggleScoreVisibility(matchId: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is DashboardState.Success) {
                val match = currentState.matches.find { it.id == matchId }
                if (match != null) {
                    val newHiddenStatus = !match.isHidden
                    repository.toggleMatchHidden(matchId, newHiddenStatus)
                    
                    val updatedList = currentState.matches.map {
                        if (it.id == matchId) it.copy(isHidden = newHiddenStatus) else it
                    }
                    _uiState.value = DashboardState.Success(updatedList)
                }
            }
        }
    }
}
