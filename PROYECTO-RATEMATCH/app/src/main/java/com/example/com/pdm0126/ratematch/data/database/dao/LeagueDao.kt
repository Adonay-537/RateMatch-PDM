package com.example.com.pdm0126.ratematch.data.database.dao

import androidx.room.*
import com.example.com.pdm0126.ratematch.data.database.entities.LeagueEntity
import com.example.com.pdm0126.ratematch.data.database.entities.LeagueWithMatches
import kotlinx.coroutines.flow.Flow

@Dao
interface LeagueDao {
    @Transaction
    @Query("SELECT * FROM leagues")
    fun getLeaguesWithMatches(): Flow<List<LeagueWithMatches>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeague(league: LeagueEntity)
}