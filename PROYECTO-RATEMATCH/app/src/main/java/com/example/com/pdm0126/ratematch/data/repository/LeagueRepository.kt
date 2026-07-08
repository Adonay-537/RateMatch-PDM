package com.example.com.pdm0126.ratematch.data.repository

import com.example.com.pdm0126.ratematch.data.database.dao.LeagueDao
import com.example.com.pdm0126.ratematch.data.remote.FootballApiService
import com.example.com.pdm0126.ratematch.data.remote.dto.TeamDto
import com.example.com.pdm0126.ratematch.data.remote.dto.LeagueTeamsResponse

class LeagueRepository(
    private val leagueDao: LeagueDao,
    private val apiService: FootballApiService
) {
    suspend fun getTeamsForLeague(competitionId: Int): List<TeamDto> {
        return try {
            val apiResponse: LeagueTeamsResponse = apiService.getTeams(competitionId)
            apiResponse.response.map { it.team }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getNationalTeams(): List<TeamDto> {
        return try {
            val apiResponse: LeagueTeamsResponse = apiService.getNationalTeams()
            apiResponse.response.map { it.team }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
