package com.example.com.pdm0126.ratematch.data.remote

import android.util.Log
import com.example.com.pdm0126.ratematch.BuildConfig
import com.example.com.pdm0126.ratematch.data.remote.dto.LeagueTeamsResponse
import com.example.com.pdm0126.ratematch.data.remote.dto.MatchDto
import com.example.com.pdm0126.ratematch.data.remote.dto.MatchesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class FootballApiService(private val client: HttpClient = KtorClient.client) {

    suspend fun getMatches(date: String): List<MatchDto> {
        return try {
            Log.d("FootballAPI", "Buscando partidos para: $date")
            
            // Verificamos si el token se inyectó correctamente
            if (BuildConfig.API_TOKEN.isEmpty()) {
                Log.e("FootballAPI", "❌ ERROR: El API_TOKEN está vacío en BuildConfig. Revisa local.properties")
            }

            // Usamos ruta relativa "matches" para heredar Base URL y Token de KtorClient
            val response: MatchesResponse = client.get("matches") {
                url {
                    parameters.append("dateFrom", date)
                    parameters.append("dateTo", date)
                }
            }.body() //Reivsando

            if (response.message != null) {
                Log.w("FootballAPI", "⚠️ Mensaje de la API: ${response.message}")
            }

            val listaDePartidos = response.matches
            
            if (listaDePartidos.isEmpty()) {
                Log.i("FootballAPI", "ℹ️ No hay partidos para la fecha $date en tus ligas suscritas.")
            } else {
                Log.d("FootballAPI", "¡Éxito! Partidos recibidos: ${listaDePartidos.size}")
            }
            listaDePartidos
        } catch (e: Exception) {
            Log.e("FootballAPI", "💥 ERROR en getMatches: ${e.message}")
            emptyList()
        }
    }

    suspend fun getTeams(competitionId: Int): LeagueTeamsResponse {
        return try {
            client.get("competitions/$competitionId/teams").body()
        } catch (e: Exception) {
            Log.e("FootballAPI", "💥 ERROR en getTeams: ${e.message}")
            LeagueTeamsResponse(emptyList())
        }
    }

    suspend fun getNationalTeams(): LeagueTeamsResponse {
        return try {
            Log.d("FootballAPI", "Buscando selecciones nacionales...")
            // Usamos ruta relativa para heredar la configuración del cliente
            client.get("teams?type=NATIONAL").body()
        } catch (e: Exception) {
            Log.e("FootballAPI", "💥 ERROR en getNationalTeams: ${e.message}")
            LeagueTeamsResponse(emptyList())
        }
    }
}
