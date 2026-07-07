package com.example.com.pdm0126.ratematch.data.repository

import com.example.com.pdm0126.ratematch.data.database.dao.MatchDao
import com.example.com.pdm0126.ratematch.data.database.entities.toEntity
import com.example.com.pdm0126.ratematch.data.database.entities.toModel
import com.example.com.pdm0126.ratematch.data.model.Match
import com.example.com.pdm0126.ratematch.data.remote.FootballApiService
import com.example.com.pdm0126.ratematch.data.remote.dto.EventDto
import com.example.com.pdm0126.ratematch.data.remote.dto.TeamStatisticsDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.TimeZone

class MatchRepository(
    private val matchDao: MatchDao,
    private val apiService: FootballApiService
) {
    /**
     * Consultamos la API usando la zona horaria del dispositivo.
     * Esto hace que la API nos devuelva exactamente los partidos que caen en ese día local.
     */
    suspend fun getMatchesForDate(date: String): List<Match> {
        val timezone = TimeZone.getDefault().id
        android.util.Log.d("MatchRepository", "Consultando API-Football para fecha: $date | Timezone: $timezone")
        
        val results = apiService.getMatchesByDate(date, timezone)
        
        if (results.isNotEmpty()) {
            val mappedMatches = results.map { resource ->
                Match(
                    id = resource.fixture.id,
                    homeTeam = resource.teams.home.name,
                    awayTeam = resource.teams.away.name,
                    scoreHome = resource.goals.home ?: 0,
                    scoreAway = resource.goals.away ?: 0,
                    status = resource.fixture.status.short ?: "NS",
                    utcDate = resource.fixture.date, // La API la devuelve ya con el offset de la zona horaria
                    leagueId = resource.league.id,
                    leagueName = resource.league.name,
                    leagueLogo = resource.league.logo ?: "",
                    homeLogo = resource.teams.home.logo ?: "",
                    awayLogo = resource.teams.away.logo ?: ""
                )
            }
            
            // Guardamos en Room para persistencia
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
}
