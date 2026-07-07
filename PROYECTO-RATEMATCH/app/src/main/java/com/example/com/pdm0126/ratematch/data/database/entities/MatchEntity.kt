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
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val scoreHome: Int,
    val scoreAway: Int,
    val status: String,
    val isHidden: Boolean,
    val isFavorite: Boolean,
    val leagueId: Int,
    val leagueName: String,
    val leagueLogo: String,
    val homeLogo: String,
    val awayLogo: String,
    val utcDate: String
)

fun MatchEntity.toModel(): Match {
    return Match(
        id = id, homeTeam = homeTeam, awayTeam = awayTeam,
        scoreHome = scoreHome, scoreAway = scoreAway,
        status = status, isHidden = isHidden, isFavorite = isFavorite, 
        leagueId = leagueId, leagueName = leagueName, leagueLogo = leagueLogo,
        homeLogo = homeLogo, awayLogo = awayLogo, utcDate = utcDate
    )
}

fun Match.toEntity(): MatchEntity {
    return MatchEntity(
        id = id, homeTeam = homeTeam, awayTeam = awayTeam,
        scoreHome = scoreHome, scoreAway = scoreAway,
        status = status, isHidden = isHidden, isFavorite = isFavorite, 
        leagueId = leagueId, leagueName = leagueName, leagueLogo = leagueLogo,
        homeLogo = homeLogo, awayLogo = awayLogo, utcDate = utcDate
    )
}
