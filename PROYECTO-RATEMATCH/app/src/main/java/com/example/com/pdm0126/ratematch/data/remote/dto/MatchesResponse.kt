package com.example.com.pdm0126.ratematch.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchesResponse(
    // Usamos una lista vacía por defecto para que no explote si la API manda un error
    val matches: List<MatchDto> = emptyList(),
    val message: String? = null,
    val errorCode: Int? = null
)
