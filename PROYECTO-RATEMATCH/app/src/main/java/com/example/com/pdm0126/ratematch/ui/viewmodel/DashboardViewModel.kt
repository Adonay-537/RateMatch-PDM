package com.example.com.pdm0126.ratematch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.com.pdm0126.ratematch.data.model.Match
import com.example.com.pdm0126.ratematch.data.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        loadMatches()
    }

    fun loadMatches() {
        viewModelScope.launch {
            _uiState.value = DashboardState.Loading
            try {
                val matches = repository.getMatchesForDate(_selectedDate.value.toString())
                val sortedMatches = matches.sortedWith(
                    compareBy<Match> { getLeaguePriority(it.leagueName) }
                        .thenBy { it.utcDate }
                )
                _uiState.value = DashboardState.Success(sortedMatches)
            } catch (e: Exception) {
                _uiState.value = DashboardState.Error(e.message ?: "Error")
            }
        }
    }

    private fun getLeaguePriority(name: String): Int {
        return when {
            name.contains("World Cup", ignoreCase = true) || name.contains("Mundial", ignoreCase = true) -> 1
            name.contains("Euro", ignoreCase = true) -> 2
            name.contains("Copa América", ignoreCase = true) || name.contains("Copa America", ignoreCase = true) -> 3
            name.contains("Champions League", ignoreCase = true) -> 4
            name.contains("Libertadores", ignoreCase = true) -> 5
            name.contains("LaLiga", ignoreCase = true) || name.contains("La Liga", ignoreCase = true) || name.contains("Primera Division", ignoreCase = true) -> 6
            name.contains("Premier League", ignoreCase = true) -> 7
            name.contains("Bundesliga", ignoreCase = true) -> 8
            name.contains("Serie A", ignoreCase = true) && !name.contains("Brazil", ignoreCase = true) -> 9
            name.contains("Ligue 1", ignoreCase = true) || name.contains("Ligue One", ignoreCase = true) -> 10
            name.contains("Brasileirão", ignoreCase = true) || name.contains("Série A", ignoreCase = true) -> 11
            else -> 100
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
        val currentState = _uiState.value
        if (currentState is DashboardState.Success) {
            val updatedList = currentState.matches.map { 
                if (it.id == matchId) it.copy(isHidden = !it.isHidden) else it 
            }
            _uiState.value = DashboardState.Success(updatedList)
        }
    }
}