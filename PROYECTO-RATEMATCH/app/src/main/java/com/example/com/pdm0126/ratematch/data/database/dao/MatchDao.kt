package com.example.com.pdm0126.ratematch.data.database.dao

import androidx.room.*
import com.example.com.pdm0126.ratematch.data.database.entities.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches WHERE leagueId = :leagueId")
    fun getMatchesForLeague(leagueId: Int): Flow<List<MatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Update
    suspend fun updateMatch(match: MatchEntity)
}