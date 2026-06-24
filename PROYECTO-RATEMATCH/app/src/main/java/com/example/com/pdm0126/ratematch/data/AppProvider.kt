package com.example.com.pdm0126.ratematch.data

import android.content.Context
import com.example.com.pdm0126.ratematch.data.database.AppDatabase

class AppProvider(context: Context) {

    private val appDatabase = AppDatabase.getDatabase(context)


    val leagueDao = appDatabase.leagueDao()
    val matchDao = appDatabase.matchDao()

    // TODO: Agregar Repositories
}