package com.example.com.pdm0126.ratematch.data.repository

import com.example.com.pdm0126.ratematch.data.database.dao.LeagueDao
import com.example.com.pdm0126.ratematch.data.database.entities.LeagueEntity
import com.example.com.pdm0126.ratematch.data.database.entities.toModel
import com.example.com.pdm0126.ratematch.data.model.League
import com.example.com.pdm0126.ratematch.data.model.LeagueWithMatches
import com.example.com.pdm0126.ratematch.data.database.entities.toModel as toModelEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LeagueRepository(private val leagueDao: LeagueDao) {

    fun getLeaguesWithMatches(): Flow<List<LeagueWithMatches>> {
        return leagueDao.getLeaguesWithMatches().map { list ->
            list.map { it.toModel() }
        }
    }

    suspend fun insertLeague(league: League) {
        val entity = LeagueEntity(
            id = league.id,
            name = league.name,
            country = league.country
        )
        leagueDao.insertLeague(entity)
    }
}