package com.example.com.pdm0126.ratematch.data.remote

import android.util.Log
import com.example.com.pdm0126.ratematch.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json

object KtorClient {
    private val _apiKeyFlow = MutableStateFlow(BuildConfig.API_TOKEN)
    val apiKeyFlow: StateFlow<String> = _apiKeyFlow

    fun updateApiKey(newKey: String) {
        if (newKey != _apiKeyFlow.value) {
            Log.d("KtorClient", "API Key actualizada: ${newKey.take(4)}...")
            _apiKeyFlow.value = newKey
        }
    }

    fun getApiKey(): String = _apiKeyFlow.value

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        // NO ponemos URL por defecto aquí para evitar conflictos con la API vieja
        // Configuramos los headers en cada petición desde el Service
    }
}
