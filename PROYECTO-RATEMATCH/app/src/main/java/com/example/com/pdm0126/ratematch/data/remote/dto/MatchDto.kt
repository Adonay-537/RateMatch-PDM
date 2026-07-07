package com.example.com.pdm0126.ratematch.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApiFootballResponse(
    val response: List<ApiMatchResource> = emptyList(),
    // Usamos JsonElement para que no falle si la API manda [] o {}
    val errors: JsonElement? = null
)

@Serializable
data class ApiMatchResource(
    val fixture: FixtureDto,
    val league: ApiLeagueDto,
    val teams: TeamsDto,
    val goals: GoalsDto,
    val score: ScoreDto? = null,
    val events: List<EventDto>? = emptyList()
)

@Serializable
data class EventDto(
    val time: EventTimeDto,
    val team: TeamDto,
    val player: PlayerDto,
    val assist: PlayerDto? = null,
    val type: String,
    val detail: String? = null
)

@Serializable
data class EventTimeDto(
    val elapsed: Int,
    val extra: Int? = null
)

@Serializable
data class PlayerDto(
    val id: Int? = null,
    val name: String? = null
)

@Serializable
data class ApiLeagueDto(
    val id: Int,
    val name: String,
    val country: String? = null,
    val logo: String? = null,
    val flag: String? = null,
    val season: Int? = null,
    val round: String? = null
)

@Serializable
data class FixtureDto(
    val id: Int,
    val date: String,
    val status: StatusDto
)

@Serializable
data class StatusDto(
    val short: String? = null
)

@Serializable
data class TeamsDto(
    val home: TeamDto,
    val away: TeamDto
)

@Serializable
data class TeamDto(
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
data class ScoreDto(
    val fulltime: GoalsDto? = null
)

@Serializable
data class LeagueTeamsResponse(
    val response: List<TeamWrapperDto> = emptyList(),
    val errors: JsonElement? = null
)

@Serializable
data class TeamWrapperDto(
    val team: TeamDto
)
