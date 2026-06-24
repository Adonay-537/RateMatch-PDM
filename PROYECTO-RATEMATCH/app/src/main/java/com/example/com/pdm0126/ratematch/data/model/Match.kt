package com.example.com.pdm0126.ratematch.data.model

data class Match(
    val id: Int = 0,
    val homeTeam: String,
    val awayTeam: String,
    val scoreHome: Int = 0,
    val scoreAway: Int = 0,
    val status: String,
    val isHidden: Boolean = false, // Para el Modo Oculto Anti-Spoiler
    val leagueId: Int = 0
)