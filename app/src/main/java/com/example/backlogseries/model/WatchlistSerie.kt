package com.example.backlogseries.model

import androidx.compose.runtime.mutableStateOf
import java.io.Serializable
import kotlin.math.ceil

data class WatchlistSerie(
    val serie: Serie,
    var minutesPerDay: Int = 60
) : Serializable {

    fun getDaysToComplete(): Int {
        val totalEpisodes = serie.number_of_episodes ?: 0
        val calculated = mutableStateOf(false)
        val episodeRuntime = serie.episode_run_time?.firstOrNull() ?: 45 // Durée moyenne par défaut

        val totalRuntime = totalEpisodes * episodeRuntime
        return if (minutesPerDay > 0) {
            ceil(totalRuntime.toDouble() / minutesPerDay).toInt()
        } else {
            0
        }
    }

    fun getWeeksToComplete(): Double {
        val days = getDaysToComplete()
        return days / 7.0
    }
}
