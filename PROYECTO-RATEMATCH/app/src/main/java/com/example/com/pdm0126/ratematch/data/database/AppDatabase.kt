package com.example.com.pdm0126.ratematch.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.com.pdm0126.ratematch.data.database.dao.LeagueDao
import com.example.com.pdm0126.ratematch.data.database.dao.MatchDao
import com.example.com.pdm0126.ratematch.data.database.entities.LeagueEntity
import com.example.com.pdm0126.ratematch.data.database.entities.MatchEntity

@Database(
    entities = [LeagueEntity::class, MatchEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun leagueDao(): LeagueDao
    abstract fun matchDao(): MatchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = AppDatabase::class.java,
                    name = "ratematch_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}