package com.example.com.pdm0126.ratematch.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class LeagueWithMatches(
    @Embedded val league: LeagueEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "leagueId"
    )
    val matches: List<MatchEntity>
)