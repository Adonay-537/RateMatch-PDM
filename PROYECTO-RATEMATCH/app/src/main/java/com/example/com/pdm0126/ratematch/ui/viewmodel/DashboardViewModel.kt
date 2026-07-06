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
                _uiState.value = DashboardState.Success(matches)
            } catch (e: Exception) {
                _uiState.value = DashboardState.Error(e.message ?: "Error")
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

    fun toggleMatchHidden(matchId: Int) {
        val currentState = _uiState.value
        if (currentState is DashboardState.Success) {
            val updatedList = currentState.matches.filter { it.id != matchId }
            _uiState.value = DashboardState.Success(updatedList)
        }
    }
}