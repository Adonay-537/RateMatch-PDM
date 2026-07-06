package com.example.com.pdm0126.ratematch.data.remote

import com.example.com.pdm0126.ratematch.data.remote.dto.MatchDto
import com.example.com.pdm0126.ratematch.data.remote.dto.MatchesResponse
import com.example.com.pdm0126.ratematch.data.remote.dto.LeagueTeamsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

class FootballApiService(private val client: HttpClient = KtorClient.client) {
    
    // Cambiamos el endpoint a v4 y usamos dateFrom/dateTo para filtrar por un solo día
    // También añadimos el header de autenticación
    suspend fun getMatches(date: String): List<MatchDto> {
        return try {
            val response: MatchesResponse = client.get("https://api.football-data.org/v4/matches") {
                header("X-Auth-Token", "8937397c72444c139c80d19f85c7c25c")
                url {
                    parameters.append("dateFrom", date)
                    parameters.append("dateTo", date)
                }
            }.body()
            response.matches
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTeams(competitionId: Int): LeagueTeamsResponse {
        return try {
            client.get("https://api.football-data.org/v4/competitions/$competitionId/teams") {
                header("X-Auth-Token", "8937397c72444c139c80d19f85c7c25c")
            }.body()
        } catch (e: Exception) {
            LeagueTeamsResponse(emptyList())
        }
    }

    suspend fun getNationalTeams(): LeagueTeamsResponse {
        return try {
            client.get("https://api.football-data.org/v4/teams?type=NATIONAL") {
                header("X-Auth-Token", "8937397c72444c139c80d19f85c7c25c")
            }.body()
        } catch (e: Exception) {
            LeagueTeamsResponse(emptyList())
        }
    }
}
