package com.example.com.pdm0126.ratematch.data

import android.content.Context
import com.example.com.pdm0126.ratematch.data.database.AppDatabase
import com.example.com.pdm0126.ratematch.data.remote.FootballApiService
import com.example.com.pdm0126.ratematch.data.remote.KtorClient
import com.example.com.pdm0126.ratematch.data.repository.*
import com.google.firebase.auth.FirebaseAuth

class AppProvider(context: Context) {
    private val appDatabase = AppDatabase.getDatabase(context)
    private val apiService = FootballApiService(KtorClient.client)
    private val firebaseAuth = FirebaseAuth.getInstance()

    val matchRepository = MatchRepository(appDatabase.matchDao(), apiService)
    val leagueRepository = LeagueRepository(appDatabase.leagueDao(), apiService)
    val userRepository = UserRepository(appDatabase.userDao())
    val authRepository = AuthRepository(firebaseAuth)
}
