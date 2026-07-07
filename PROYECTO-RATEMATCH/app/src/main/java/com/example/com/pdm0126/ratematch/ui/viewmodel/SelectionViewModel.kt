package com.example.com.pdm0126.ratematch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.com.pdm0126.ratematch.data.repository.LeagueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SelectableItem(
    val id: Int,
    val name: String,
    val category: String,
    val isSelected: Boolean = false
)

class SelectionViewModel(private val leagueRepository: LeagueRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<SelectableItem>>(emptyList())
    val items: StateFlow<List<SelectableItem>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadTeams(competitionId: Int = 2014) {
        viewModelScope.launch {
            _isLoading.value = true
            val teams = leagueRepository.getTeamsForLeague(competitionId)
            _items.value = teams.map {
                SelectableItem(
                    id = it.id,
                    name = it.name,
                    category = "Equipos"
                )
            }
            _isLoading.value = false
        }
    }

    fun loadNationalTeams() {
        viewModelScope.launch {
            _isLoading.value = true
            val teams = leagueRepository.getNationalTeams()
            _items.value = teams.map {
                SelectableItem(
                    id = it.id,
                    name = it.name,
                    category = "Selecciones"
                )
            }
            _isLoading.value = false
        }
    }

    fun toggleSelection(id: Int) {
        _items.value = _items.value.map {
            if (it.id == id) it.copy(isSelected = !it.isSelected) else it
        }
    }
}
