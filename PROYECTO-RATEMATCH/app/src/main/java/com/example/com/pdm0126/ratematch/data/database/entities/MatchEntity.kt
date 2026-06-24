package com.example.com.pdm0126.ratematch.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.com.pdm0126.ratematch.data.model.Match

@Entity(
    tableName = "matches",
    foreignKeys = [
        ForeignKey(
            entity = LeagueEntity::class,
            parentColumns = ["id"],
            childColumns = ["leagueId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("leagueId")]
)
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val homeTeam: String,
    val awayTeam: String,
    val scoreHome: Int,
    val scoreAway: Int,
    val status: String,
    val isHidden: Boolean,
    val leagueId: Int
)

fun MatchEntity.toModel(): Match {
    return Match(
        id = id, homeTeam = homeTeam, awayTeam = awayTeam,
        scoreHome = scoreHome, scoreAway = scoreAway,
        status = status, isHidden = isHidden, leagueId = leagueId
    )
}