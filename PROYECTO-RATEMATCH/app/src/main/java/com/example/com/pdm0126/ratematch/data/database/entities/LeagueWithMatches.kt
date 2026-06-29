package com.example.com.pdm0126.ratematch.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.com.pdm0126.ratematch.data.model.LeagueWithMatches as LeagueWithMatchesModel

data class LeagueWithMatches(
    @Embedded val league: LeagueEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "leagueId"
    )
    val matches: List<MatchEntity>
)

fun LeagueWithMatches.toModel(): LeagueWithMatchesModel {
    return LeagueWithMatchesModel(
        league = league.toModel(),
        matches = matches.map { it.toModel() }
    )
}