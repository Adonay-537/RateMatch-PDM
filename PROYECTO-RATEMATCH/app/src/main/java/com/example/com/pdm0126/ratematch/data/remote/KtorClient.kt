package com.example.com.pdm0126.ratematch.data.remote

import android.util.Log
import com.example.com.pdm0126.ratematch.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {

    val client = HttpClient(OkHttp) {

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("KtorClient", message)
                }
            }
            level = LogLevel.ALL
        }

        defaultRequest {
            url("https://api.football-data.org/v4/")
            header("X-Auth-Token", BuildConfig.API_TOKEN)
        }
    }
}