package com.example.com.pdm0126.ratematch.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.com.pdm0126.ratematch.data.model.League

@Entity(tableName = "leagues")
data class LeagueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val country: String
)

fun LeagueEntity.toModel(): League {
    return League(id = id, name = name, country = country)
}

fun League.toEntity(): LeagueEntity {
    return LeagueEntity(id = id, name = name, country = country)
}