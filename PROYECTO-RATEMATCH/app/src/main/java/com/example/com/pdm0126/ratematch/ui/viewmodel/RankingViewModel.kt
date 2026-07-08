package com.example.com.pdm0126.ratematch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RankedMatch(
    val matchId: Int,
    val homeTeam: String,
    val awayTeam: String,
    val scoreHome: Int,
    val scoreAway: Int,
    val leagueName: String,
    val userRating: Int
)

sealed class RankingState {
    data object Loading : RankingState()
    data class Success(val matches: List<RankedMatch>) : RankingState()
    data class Error(val message: String) : RankingState()
}

class RankingViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow<RankingState>(RankingState.Loading)
    val uiState: StateFlow<RankingState> = _uiState

    init {
        loadTopMatches()
    }

    fun loadTopMatches() {
        viewModelScope.launch {
            _uiState.value = RankingState.Loading
            try {
                val snapshot = firestore.collection("rated_matches")
                    .orderBy("userRating", Query.Direction.DESCENDING)
                    .limit(10)
                    .get()
                    .await()

                val matchesList = snapshot.documents.mapNotNull { doc ->
                    try {
                        RankedMatch(
                            matchId = doc.getLong("matchId")?.toInt() ?: 0,
                            homeTeam = doc.getString("homeTeam") ?: "",
                            awayTeam = doc.getString("awayTeam") ?: "",
                            scoreHome = doc.getLong("scoreHome")?.toInt() ?: 0,
                            scoreAway = doc.getLong("scoreAway")?.toInt() ?: 0,
                            leagueName = doc.getString("leagueName") ?: "",
                            userRating = doc.getLong("userRating")?.toInt() ?: 0
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                _uiState.value = RankingState.Success(matchesList)
            } catch (e: Exception) {
                _uiState.value = RankingState.Error("No se pudo cargar el ranking: ${e.message}")
            }
        }
    }
}