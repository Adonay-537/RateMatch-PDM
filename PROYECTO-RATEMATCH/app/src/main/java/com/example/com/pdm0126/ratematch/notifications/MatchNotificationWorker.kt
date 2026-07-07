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
            // 1. Obtener todos los partidos favoritos desde Room
            val favoriteMatches = repository.getAllMatches().first().filter { it.isFavorite }
            
            for (match in favoriteMatches) {
                // 2. Consultar el estado actual del partido en la API
                val updatedMatches = repository.getMatchesForDate(match.utcDate.substringBefore("T"))
                val liveMatch = updatedMatches.find { it.id == match.id }
                
                if (liveMatch != null) {
                    // 3. Si hubo un cambio de marcador (GOL)
                    if (liveMatch.scoreHome > match.scoreHome || liveMatch.scoreAway > match.scoreAway) {
                        
                        // Buscamos quién fue el último en anotar desde los eventos
                        val events = repository.getMatchEvents(match.id)
                        val lastGoal = events.filter { it.type.lowercase() == "goal" }.lastOrNull()
                        
                        val teamName = if (liveMatch.scoreHome > match.scoreHome) liveMatch.homeTeam else liveMatch.awayTeam
                        val scorer = lastGoal?.player?.name ?: "Jugador"
                        val scoreText = "${liveMatch.homeTeam} [${liveMatch.scoreHome}] - ${liveMatch.scoreAway} ${liveMatch.awayTeam}"
                        
                        // 4. Lanzar la notificación
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
