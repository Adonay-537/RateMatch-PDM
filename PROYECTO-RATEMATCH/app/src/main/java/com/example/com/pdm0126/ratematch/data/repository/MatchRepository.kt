package com.example.com.pdm0126.ratematch.data.repository

import android.util.Log
import com.example.com.pdm0126.ratematch.data.database.dao.MatchDao
import com.example.com.pdm0126.ratematch.data.database.entities.MatchEntity
import com.example.com.pdm0126.ratematch.data.database.entities.toModel
import com.example.com.pdm0126.ratematch.data.model.Match
import com.example.com.pdm0126.ratematch.data.remote.KtorClient
import com.example.com.pdm0126.ratematch.data.remote.dto.FootballMatchesResponseDto
import com.example.com.pdm0126.ratematch.data.remote.dto.toModel
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MatchRepository(private val matchDao: MatchDao) {

    fun getMatchesForLeague(leagueId: Int): Flow<List<Match>> {
        return matchDao.getMatchesForLeague(leagueId).map { list ->
            list.map { it.toModel() }
        }
    }

    suspend fun fetchAndSyncMatches(): Result<Unit> {
        return try {

            val response: FootballMatchesResponseDto = KtorClient.client
                .get("matches")
                .body()

            response.matches.forEach { apiMatch ->

                val leagueId = apiMatch.competition?.id ?: 1
                val domainMatch = apiMatch.toModel(leagueId)

                matchDao.insertMatch(domainMatch.toEntity())
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MatchRepository", "Error de red o sincronización: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun insertMatch(match: Match) {
        matchDao.insertMatch(match.toEntity())
    }

    suspend fun updateMatch(match: Match) {
        matchDao.updateMatch(match.toEntity())
    }

    private fun Match.toEntity(): MatchEntity {
        return MatchEntity(
            id = id,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            scoreHome = scoreHome,
            scoreAway = scoreAway,
            status = status,
            isHidden = isHidden,
            leagueId = leagueId
        )
    }
}


