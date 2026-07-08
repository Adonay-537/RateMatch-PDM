package com.example.com.pdm0126.ratematch.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.com.pdm0126.ratematch.RateMatchApplication
import kotlinx.coroutines.flow.first

class MatchNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        val app = applicationContext as RateMatchApplication
        val repository = app.appProvider.matchRepository
        val notificationHelper = NotificationHelper(applicationContext)

        try {
            val favoriteMatches = repository.getAllMatches().first().filter { it.isFavorite }
            
            for (match in favoriteMatches) {
                val updatedMatches = repository.getMatchesForDate(match.utcDate.substringBefore("T"))
                val liveMatch = updatedMatches.find { it.id == match.id }
                
                if (liveMatch != null) {
                    if (liveMatch.scoreHome > match.scoreHome || liveMatch.scoreAway > match.scoreAway) {
                        
                        val events = repository.getMatchEvents(match.id)
                        val lastGoal = events.filter { it.type.lowercase() == "goal" }.lastOrNull()
                        
                        val teamName = if (liveMatch.scoreHome > match.scoreHome) liveMatch.homeTeam else liveMatch.awayTeam
                        val scorer = lastGoal?.player?.name ?: "Jugador"
                        val scoreText = "${liveMatch.homeTeam} [${liveMatch.scoreHome}] - ${liveMatch.scoreAway} ${liveMatch.awayTeam}"
                        
                        notificationHelper.showGoalNotification(teamName, scorer, scoreText)
                        
                        Log.d("NotificationWorker", "¡Notificación enviada para el gol de $scorer!")
                    }
                }
            }
            return androidx.work.ListenableWorker.Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error comprobando goles: ${e.message}")
            return androidx.work.ListenableWorker.Result.retry()
        }
    }
}
