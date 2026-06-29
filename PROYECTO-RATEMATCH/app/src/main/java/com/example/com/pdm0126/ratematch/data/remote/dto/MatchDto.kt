package com.example.com.pdm0126.ratematch.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FootballMatchesResponseDto(
    val matches: List<ApiMatchDto>
)

@Serializable
data class ApiMatchDto(
    val id: Int,
    val status: String,
    val homeTeam: ApiTeamDto,
    val awayTeam: ApiTeamDto,
    val score: ApiScoreDto,
    val competition: ApiCompetitionDto? = null
)

@Serializable
data class ApiTeamDto(
    val name: String
)

@Serializable
data class ApiScoreDto(
    val fullTime: ApiTimeScoreDto
)

@Serializable
data class ApiTimeScoreDto(
    val home: Int?,
    val away: Int?
)

@Serializable
data class ApiCompetitionDto(
    val id: Int,
    val name: String
)


fun ApiMatchDto.toModel(detectedLeagueId: Int): com.example.com.pdm0126.ratematch.data.model.Match {
    return com.example.com.pdm0126.ratematch.data.model.Match(
        id = this.id,
        homeTeam = this.homeTeam.name,
        awayTeam = this.awayTeam.name,
        scoreHome = this.score.fullTime.home ?: 0,
        scoreAway = this.score.fullTime.away ?: 0,
        status = this.status,
        isHidden = false,
        leagueId = detectedLeagueId
    )
}