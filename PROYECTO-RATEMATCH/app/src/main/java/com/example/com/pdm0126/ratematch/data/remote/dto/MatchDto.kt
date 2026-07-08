package com.example.com.pdm0126.ratematch.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApiFootballResponse(
    val response: List<ApiMatchResource> = emptyList(),
    val errors: JsonElement? = null
)

@Serializable
data class ApiMatchResource(
    val fixture: FixtureDto,
    val league: LeagueDto,
    val teams: FixtureTeamsDto,
    val goals: GoalsDto
)

@Serializable
data class FixtureDto(
    val id: Int,
    val date: String,
    val status: StatusDto
)

@Serializable
data class StatusDto(
    val short: String?
)

@Serializable
data class LeagueDto(
    val id: Int,
    val name: String,
    val logo: String? = null
)

@Serializable
data class FixtureTeamsDto(
    val home: TeamDetailDto,
    val away: TeamDetailDto
)

@Serializable
data class TeamDetailDto(
    val id: Int,
    val name: String,
    val logo: String? = null
)

@Serializable
data class GoalsDto(
    val home: Int? = null,
    val away: Int? = null
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
    val id: Int? = null,
    val name: String?,
    val shortName: String? = null,
    val logo: String? = null,
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
data class TeamWrapperDto(
    val team: TeamDto
)

@Serializable
data class LeagueTeamsResponse(
    val response: List<TeamWrapperDto> = emptyList(),
    val errors: JsonElement? = null
)
