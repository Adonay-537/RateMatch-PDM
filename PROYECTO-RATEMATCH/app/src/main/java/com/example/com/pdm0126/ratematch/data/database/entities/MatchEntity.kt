package com.example.com.pdm0126.ratematch.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.com.pdm0126.ratematch.data.model.Match

@Entity(
    tableName = "matches",
    indices = [Index("leagueId")]
)
data class MatchEntity(
    @PrimaryKey(autoGenerate = false) // Usamos el ID de la API
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val scoreHome: Int,
    val scoreAway: Int,
    val status: String,
    val isHidden: Boolean,
    val isFavorite: Boolean,
    val leagueId: Int
)

fun MatchEntity.toModel(): Match {
    return Match(
        id = id, homeTeam = homeTeam, awayTeam = awayTeam,
        scoreHome = scoreHome, scoreAway = scoreAway,
        status = status, isHidden = isHidden, isFavorite = isFavorite, leagueId = leagueId
    )
}

fun Match.toEntity(): MatchEntity {
    return MatchEntity(
        id = id, homeTeam = homeTeam, awayTeam = awayTeam,
        scoreHome = scoreHome, scoreAway = scoreAway,
        status = status, isHidden = isHidden, isFavorite = isFavorite, leagueId = leagueId
    )
}
