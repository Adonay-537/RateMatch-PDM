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
data class TeamStatisticsDto(
    val team: TeamDto,
    val statistics: List<StatisticItemDto>
)

@Serializable
data class StatisticItemDto(
    val type: String,
    val value: JsonElement? = null
)
