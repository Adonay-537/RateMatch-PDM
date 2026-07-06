package com.example.com.pdm0126.ratematch.data.repository

import com.example.com.pdm0126.ratematch.data.database.dao.LeagueDao
import com.example.com.pdm0126.ratematch.data.remote.FootballApiService
import com.example.com.pdm0126.ratematch.data.remote.dto.ApiTeamDto

class LeagueRepository(
    private val leagueDao: LeagueDao,
    private val apiService: FootballApiService
) {
    suspend fun getTeamsForLeague(competitionId: Int): List<ApiTeamDto> {
        return try {
            apiService.getTeams(competitionId).teams
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getNationalTeams(): List<ApiTeamDto> {
        return try {
            apiService.getNationalTeams().teams
        } catch (e: Exception) {
            emptyList()
        }
    }
}