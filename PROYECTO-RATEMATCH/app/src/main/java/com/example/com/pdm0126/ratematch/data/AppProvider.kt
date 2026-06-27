package com.example.com.pdm0126.ratematch.data

import android.content.Context
import com.example.com.pdm0126.ratematch.data.database.AppDatabase
import com.example.com.pdm0126.ratematch.data.repository.LeagueRepository
import com.example.com.pdm0126.ratematch.data.repository.MatchRepository

class AppProvider(context: Context) {
    private val appDatabase = AppDatabase.getDatabase(context)

    val leagueRepository = LeagueRepository(appDatabase.leagueDao())
    val matchRepository = MatchRepository(appDatabase.matchDao())
}