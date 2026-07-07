package com.example.com.pdm0126.ratematch.data.model

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.LocalDate

data class Match(
    val id: Int = 0,
    val homeTeam: String,
    val awayTeam: String,
    val scoreHome: Int = 0,
    val scoreAway: Int = 0,
    val status: String,
    val isHidden: Boolean = false,
    val isFavorite: Boolean = false,
    val leagueId: Int = 0,
    val leagueName: String = "",
    val leagueLogo: String = "",
    val homeLogo: String = "",
    val awayLogo: String = "",
    val utcDate: String = ""
) {
    // Como ahora pedimos la zona horaria a la API, la fecha ya viene ajustada.
    // Solo necesitamos darle formato para mostrarla.
    fun getLocalTime(): String {
        return try {
            val zonedDateTime = ZonedDateTime.parse(utcDate)
            zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            ""
        }
    }

    fun getLocalDate(): String {
        return try {
            val zonedDateTime = ZonedDateTime.parse(utcDate)
            zonedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            ""
        }
    }

    fun isSameDay(date: LocalDate): Boolean {
        return try {
            val zonedDateTime = ZonedDateTime.parse(utcDate)
            zonedDateTime.toLocalDate().isEqual(date)
        } catch (e: Exception) {
            false
        }
    }
}
