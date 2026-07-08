package com.example.com.pdm0126.ratematch.data.remote

import android.util.Log
import com.example.com.pdm0126.ratematch.data.remote.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

class FootballApiService(private val client: HttpClient = KtorClient.client) {

    private val baseUrl = "https://v3.football.api-sports.io/"

    suspend fun getMatchesByDate(date: String, timezone: String): List<ApiMatchResource> {
        return try {
            val key = KtorClient.getApiKey()
            Log.d("FootballAPI", "Petición API-Football. Fecha: $date | Timezone: $timezone")
            
            val response: ApiFootballResponse = client.get("${baseUrl}fixtures") {
                header("x-apisports-key", key)
                url {
                    parameters.append("date", date)
                    parameters.append("timezone", timezone)
                }
            }.body()

            val errorsStr = response.errors.toString()
            if (errorsStr != "[]" && errorsStr != "{}" && errorsStr != "null") {
                Log.e("FootballAPI", "❌ Error detectado: $errorsStr")
                throw Exception("Error de API: $errorsStr")
            }

            Log.d("FootballAPI", "¡Éxito! Partidos recibidos: ${response.response.size}")
            response.response
        } catch (e: Exception) {
            Log.e("FootballAPI", "💥 Error en getMatchesByDate: ${e.message}")
            throw e
        }
    }

    suspend fun getMatchStatistics(fixtureId: Int): List<TeamStatisticsDto> {
        return try {
            val response: StatisticsResponse = client.get("${baseUrl}fixtures/statistics") {
                header("x-apisports-key", KtorClient.getApiKey())
                url {
                    parameters.append("fixture", fixtureId.toString())
                }
            }.body()
            response.response
        } catch (e: Exception) {
            Log.e("FootballAPI", "💥 Error en getMatchStatistics: ${e.message}")
            emptyList()
        }
    }

    suspend fun getMatchEvents(fixtureId: Int): List<EventDto> {
        return try {
            val response: MatchEventsResponse = client.get("${baseUrl}fixtures/events") {
                header("x-apisports-key", KtorClient.getApiKey())
                url {
                    parameters.append("fixture", fixtureId.toString())
                }
            }.body()
            response.response
        } catch (e: Exception) {
            Log.e("FootballAPI", "💥 Error en getMatchEvents: ${e.message}")
            emptyList()
        }
    }

    suspend fun getTeams(competitionId: Int): LeagueTeamsResponse {
        return try {
            client.get("${baseUrl}teams") {
                header("x-apisports-key", KtorClient.getApiKey())
                url {
                    parameters.append("league", competitionId.toString())
                    parameters.append("season", "2024")
                }
            }.body()
        } catch (e: Exception) {
            LeagueTeamsResponse(emptyList())
        }
    }

    suspend fun getNationalTeams(): LeagueTeamsResponse {
        return try {
            client.get("${baseUrl}teams") {
                header("x-apisports-key", KtorClient.getApiKey())
                url {
                    parameters.append("type", "national")
                }
            }.body()
        } catch (e: Exception) {
            LeagueTeamsResponse(emptyList())
        }
    }
}
