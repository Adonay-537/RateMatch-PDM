package com.example.com.pdm0126.ratematch.data.database.dao

import androidx.room.*
import com.example.com.pdm0126.ratematch.data.database.entities.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :matchId LIMIT 1")
    suspend fun getMatchByIdInstant(matchId: Int): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Query("UPDATE matches SET isHidden = :isHidden WHERE id = :matchId")
    suspend fun updateHiddenStatus(matchId: Int, isHidden: Boolean)

    @Query("UPDATE matches SET isFavorite = :isFavorite WHERE id = :matchId")
    suspend fun updateFavoriteStatus(matchId: Int, isFavorite: Boolean)
}