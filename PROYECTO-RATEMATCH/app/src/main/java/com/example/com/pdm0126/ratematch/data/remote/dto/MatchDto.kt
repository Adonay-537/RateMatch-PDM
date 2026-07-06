package com.example.com.pdm0126.ratematch.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchesResponse(
    val matches: List<MatchDto>
)

@Serializable
data class MatchDto(
    val id: Int,
    val homeTeam: TeamDto?,
    val awayTeam: TeamDto?,
    val status: String?,
    val score: ScoreDto? = null
)

@Serializable
data class TeamDto(
    val name: String?,
    val shortName: String?,
    val crest: String? = null
)

@Serializable
data class ScoreDto(
    val fullTime: TimeScoreDto? = null
)

@Serializable
data class TimeScoreDto(
    val home: Int? = null,
    val away: Int? = null
)

@Serializable
data class ApiTeamDto(
    val id: Int,
    val name: String?,
    val shortName: String?
)

@Serializable
data class LeagueTeamsResponse(
    val teams: List<ApiTeamDto>
)
