package com.example.com.pdm0126.ratematch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.com.pdm0126.ratematch.data.model.Match
import com.example.com.pdm0126.ratematch.data.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MatchDetailState {
    data object Loading : MatchDetailState()
    data class Success(val match: Match) : MatchDetailState()
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
                val match = matchRepository.getAllMatches().collect { list ->
                    val m = list.find { it.id == matchId }
                    if (m != null) {
                        _uiState.value = MatchDetailState.Success(m)
                    } else {
                        _uiState.value = MatchDetailState.Error("Partido no encontrado")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = MatchDetailState.Error("Error al cargar el partido")
            }
        }
    }

    fun toggleFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            matchRepository.toggleMatchFavorite(matchId, isFavorite)
        }
    }
}
