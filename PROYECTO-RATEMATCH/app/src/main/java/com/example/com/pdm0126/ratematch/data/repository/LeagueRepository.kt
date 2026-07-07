package com.example.com.pdm0126.ratematch.data.repository

import com.example.com.pdm0126.ratematch.data.database.dao.LeagueDao
import com.example.com.pdm0126.ratematch.data.remote.FootballApiService
import com.example.com.pdm0126.ratematch.data.remote.dto.TeamDto

class LeagueRepository(
    private val leagueDao: LeagueDao,
    private val apiService: FootballApiService
) {
    suspend fun getTeamsForLeague(competitionId: Int): List<TeamDto> {
        return try {
            val response = apiService.getTeams(competitionId)
            response.response.map { it.team }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getNationalTeams(): List<TeamDto> {
        return try {
            val response = apiService.getNationalTeams()
            response.response.map { it.team }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
