package com.example.com.pdm0126.ratematch.data.remote

// Si esto te sale en rojo, presiona Alt + Enter para importarlo
import com.example.com.pdm0126.ratematch.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {
    val client = HttpClient(CIO) {

        // 1. Intérprete de JSON (Este ya lo tenías perfecto)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        // 2. Activamos el Logger para que imprima todo en el Logcat
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        // 3. LA LLAVE MAESTRA: Base URL y Auth Token
        defaultRequest {
            // Le decimos a dónde conectarse por defecto
            url("https://api.football-data.org/v4/")
            // Inyectamos el Token que extrajimos de tu local.properties
            header("X-Auth-Token", BuildConfig.API_TOKEN)
        }
    }
}