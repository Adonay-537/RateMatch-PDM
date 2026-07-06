package com.example.com.pdm0126.ratematch.data.repository

import com.example.com.pdm0126.ratematch.data.database.dao.MatchDao
import com.example.com.pdm0126.ratematch.data.model.Match
import com.example.com.pdm0126.ratematch.data.remote.FootballApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MatchRepository(
    private val matchDao: MatchDao,
    private val apiService: FootballApiService
) {
    suspend fun getMatchesForDate(date: String): List<Match> {
        val remoteDtos = apiService.getMatches(date)
        if (remoteDtos.isNotEmpty()) {
            return remoteDtos.map { dto ->
                Match(
                    id = dto.id,
                    homeTeam = dto.homeTeam?.name ?: dto.homeTeam?.shortName ?: "Unknown",
                    awayTeam = dto.awayTeam?.name ?: dto.awayTeam?.shortName ?: "Unknown",
                    scoreHome = dto.score?.fullTime?.home ?: 0,
                    scoreAway = dto.score?.fullTime?.away ?: 0,
                    status = dto.status ?: "SCHEDULED"
                )
            }
        }
        return emptyList()
    }

    fun getAllMatches(): Flow<List<Match>> {
        return matchDao.getAllMatches().map { entities ->
            entities.map { entity ->
                Match(
                    id = entity.id,
                    homeTeam = entity.homeTeam,
                    awayTeam = entity.awayTeam,
                    scoreHome = entity.scoreHome,
                    scoreAway = entity.scoreAway,
                    status = entity.status,
                    isFavorite = entity.isFavorite,
                    isHidden = entity.isHidden
                )
            }
        }
    }

    suspend fun toggleMatchFavorite(matchId: Int, isFavorite: Boolean) {
        matchDao.updateFavoriteStatus(matchId, isFavorite)
    }
}
