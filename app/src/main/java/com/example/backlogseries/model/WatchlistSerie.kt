package com.example.backlogseries.model

import androidx.compose.runtime.mutableStateOf
import java.io.Serializable

data class WatchlistSerie(
    val serie: Serie,
    var episodesPerWeek: Int = 3,
    var episodesPerDay: Int = 1,
    var minutesPerDay: Int = 60
) : Serializable {
    
    fun getDaysToComplete(): Int {
        val totalEpisodes = serie.number_of_episodes ?: 0
        val calculated = mutableStateOf(false)
        val totalRuntime = serie.getTotalRuntime(calculated) ?: (totalEpisodes * 45) // Estimation par défaut
        
        return when {
            episodesPerDay > 0 -> Math.ceil(totalEpisodes.toDouble() / episodesPerDay).toInt()
            minutesPerDay > 0 -> Math.ceil(totalRuntime.toDouble() / minutesPerDay).toInt()
            else -> 0
        }
    }
    
    fun getWeeksToComplete(): Double {
        val days = getDaysToComplete()
        return if (episodesPerWeek > 0) {
            days / 7.0
        } else {
            0.0
        }
    }
}
