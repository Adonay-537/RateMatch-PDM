package com.example.com.pdm0126.ratematch.data.repository

import com.example.com.pdm0126.ratematch.data.database.dao.MatchDao
import com.example.com.pdm0126.ratematch.data.database.entities.MatchEntity
import com.example.com.pdm0126.ratematch.data.database.entities.toModel
import com.example.com.pdm0126.ratematch.data.model.Match
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MatchRepository(private val matchDao: MatchDao) {

    fun getMatchesForLeague(leagueId: Int): Flow<List<Match>> {
        return matchDao.getMatchesForLeague(leagueId).map { list ->
            list.map { it.toModel() }
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