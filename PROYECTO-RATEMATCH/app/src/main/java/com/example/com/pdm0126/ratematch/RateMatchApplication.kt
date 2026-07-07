package com.example.com.pdm0126.ratematch

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.com.pdm0126.ratematch.data.AppProvider
import com.example.com.pdm0126.ratematch.notifications.MatchNotificationWorker
import java.util.concurrent.TimeUnit

class RateMatchApplication : Application() {
    val appProvider by lazy { AppProvider(this) }

    override fun onCreate() {
        super.onCreate()
        setupNotificationWorker()
    }

    private fun setupNotificationWorker() {
        // Configuramos una tarea periódica cada 15 minutos (mínimo permitido por Android)
        val workRequest = PeriodicWorkRequestBuilder<MatchNotificationWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "MatchGoalTracker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
