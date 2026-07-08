package com.example.com.pdm0126.ratematch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.com.pdm0126.ratematch.data.model.Match
import com.example.com.pdm0126.ratematch.data.repository.MatchRepository
import com.example.com.pdm0126.ratematch.data.remote.dto.EventDto
import com.example.com.pdm0126.ratematch.data.remote.dto.TeamStatisticsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MatchDetailState {
    data object Loading : MatchDetailState()
    data class Success(
        val match: Match,
        val statistics: List<TeamStatisticsDto> = emptyList(),
        val events: List<EventDto> = emptyList()
    ) : MatchDetailState()
    data class Error(val message: String) : MatchDetailState()
}

class MatchDetailViewModel(
    private val matchId: Int,
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchDetailState>(MatchDetailState.Loading)
    val uiState: StateFlow<MatchDetailState> = _uiState

    init {
        loadMatch()
    }

    private fun loadMatch() {
        viewModelScope.launch {
            try {
                matchRepository.getAllMatches().collect { list ->
                    val match = list.find { it.id == matchId }
                    if (match != null) {
                        loadStatistics(match)
                    } else {
                        val singleMatch = matchRepository.getMatchById(matchId)
                        if (singleMatch != null) {
                            loadStatistics(singleMatch)
                        } else {
                            _uiState.value = MatchDetailState.Error("Partido no encontrado")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = MatchDetailState.Error("Error al cargar el detalle: ${e.message}")
            }
        }
    }

    private suspend fun loadStatistics(match: Match) {
        try {
            val stats = matchRepository.getMatchStatistics(matchId)
            val events = matchRepository.getMatchEvents(matchId)
            _uiState.value = MatchDetailState.Success(match, stats, events)
        } catch (e: Exception) {
            _uiState.value = MatchDetailState.Success(match, emptyList(), emptyList())
        }
    }

    fun toggleFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            matchRepository.toggleMatchFavorite(matchId, isFavorite)
        }
    }

    fun rateMatch(rating: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is MatchDetailState.Success) {
                try {
                    matchRepository.rateMatch(matchId, rating, currentState.match)
                } catch (e: Exception) {
                    android.util.Log.e("MatchDetailVM", "Error guardando rating: ${e.message}")
                }
            }
        }
    }


    fun predictMatch(homeScore: Int, awayScore: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is MatchDetailState.Success) {
                try {
                    matchRepository.savePrediction(matchId, homeScore, awayScore, currentState.match)
                } catch (e: Exception) {
                    android.util.Log.e("MatchDetailVM", "Error guardando pronóstico: ${e.message}")
                }
            }
        }
    }
}