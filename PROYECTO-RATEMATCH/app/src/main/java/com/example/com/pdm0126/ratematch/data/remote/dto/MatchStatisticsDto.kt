package com.example.com.pdm0126.ratematch.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class StatisticsResponse(
    val response: List<TeamStatisticsDto> = emptyList(),
    val errors: JsonElement? = null
)

@Serializable
data class MatchEventsResponse(
    val response: List<EventDto> = emptyList(),
    val errors: JsonElement? = null
)

@Serializable
data class EventDto(
    val time: EventTimeDto,
    val team: EventTeamDto,
    val player: EventPlayerDto,
    val assist: EventPlayerDto? = null,
    val type: String,
    val detail: String,
    val comments: String? = null
)

@Serializable
data class EventTimeDto(
    val elapsed: Int,
    val extra: Int? = null
)

@Serializable
data class EventTeamDto(
    val id: Int? = null,
    val name: String? = null,
    val logo: String? = null
)

@Serializable
data class EventPlayerDto(
    val id: Int? = null,
    val name: String? = null
)

@Serializable
data class TeamStatisticsDto(
    val team: TeamDto,
    val statistics: List<StatisticItemDto>
)

@Serializable
data class StatisticItemDto(
    val type: String,
    val value: JsonElement? = null
)
