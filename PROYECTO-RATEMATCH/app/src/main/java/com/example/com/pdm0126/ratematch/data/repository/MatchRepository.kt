package com.example.com.pdm0126.ratematch.data.repository

import com.example.com.pdm0126.ratematch.data.database.dao.MatchDao
import com.example.com.pdm0126.ratematch.data.database.entities.toEntity
import com.example.com.pdm0126.ratematch.data.database.entities.toModel
import com.example.com.pdm0126.ratematch.data.model.Match
import com.example.com.pdm0126.ratematch.data.remote.FootballApiService
import com.example.com.pdm0126.ratematch.data.remote.dto.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.TimeZone

class MatchRepository(
    private val matchDao: MatchDao,
    private val apiService: FootballApiService
) {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getMatchesForDate(date: String): List<Match> {
        val timezone = TimeZone.getDefault().id
        android.util.Log.d("MatchRepository", "Consultando API-Football para fecha: $date | Timezone: $timezone")

        val results = apiService.getMatchesByDate(date, timezone)

        if (results.isNotEmpty()) {
            val mappedMatches = results.map { resource ->

                val existingMatch = matchDao.getMatchByIdInstant(resource.fixture.id)
                val savedRating = existingMatch?.userRating ?: 0

                Match(
                    id = resource.fixture.id,
                    homeTeam = resource.teams.home.name,
                    awayTeam = resource.teams.away.name,
                    scoreHome = resource.goals.home ?: 0,
                    scoreAway = resource.goals.away ?: 0,
                    status = resource.fixture.status.short ?: "NS",
                    utcDate = resource.fixture.date,
                    leagueId = resource.league.id,
                    leagueName = resource.league.name,
                    leagueLogo = resource.league.logo ?: "",
                    homeLogo = resource.teams.home.logo ?: "",
                    awayLogo = resource.teams.away.logo ?: "",
                    userRating = savedRating // Rescatamos el rating
                )
            }

            matchDao.insertMatches(mappedMatches.map { it.toEntity() })
            return mappedMatches
        }
        return emptyList()
    }

    suspend fun getMatchStatistics(matchId: Int): List<TeamStatisticsDto> {
        return apiService.getMatchStatistics(matchId)
    }

    suspend fun getMatchEvents(matchId: Int): List<EventDto> {
        return apiService.getMatchEvents(matchId)
    }

    fun getAllMatches(): Flow<List<Match>> {
        return matchDao.getAllMatches().map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun getMatchById(matchId: Int): Match? {
        return matchDao.getMatchByIdInstant(matchId)?.toModel()
    }

    suspend fun toggleMatchFavorite(matchId: Int, isFavorite: Boolean) {
        matchDao.updateFavoriteStatus(matchId, isFavorite)
    }

    suspend fun toggleMatchHidden(matchId: Int, isHidden: Boolean) {
        matchDao.updateHiddenStatus(matchId, isHidden)
    }

    // ==========================================
    // NUEVA FUNCIÓN: Calificar y Subir a Firebase
    // ==========================================
    suspend fun rateMatch(matchId: Int, rating: Int, match: Match) {
        try {
            // 1. Guardamos localmente en Room creando una copia del partido con el nuevo rating
            val updatedMatch = match.copy(userRating = rating)
            matchDao.insertMatches(listOf(updatedMatch.toEntity()))

            // 2. Subimos a Firestore (La colección "rated_matches" actuará como tu historial)
            // Solo subimos los datos que necesitamos para armar el Ranking después.
            val firestoreData = hashMapOf(
                "matchId" to matchId,
                "homeTeam" to match.homeTeam,
                "awayTeam" to match.awayTeam,
                "scoreHome" to match.scoreHome,
                "scoreAway" to match.scoreAway,
                "leagueName" to match.leagueName,
                "date" to match.utcDate,
                "userRating" to rating,
                "timestamp" to System.currentTimeMillis() // Para ordenar cronológicamente si es necesario
            )

            // Guardamos el documento usando el ID del partido para que no se duplique si lo califica dos veces
            firestore.collection("rated_matches")
                .document(matchId.toString())
                .set(firestoreData)
                .addOnSuccessListener {
                    android.util.Log.d("Firestore", "¡Partido $matchId guardado con éxito en el historial!")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("Firestore", "Error al guardar el partido: ${e.message}")
                }
        } catch (e: Exception) {
            android.util.Log.e("MatchRepository", "Error local guardando calificación: ${e.message}")
        }
    }
}